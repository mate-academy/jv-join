package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createCarQuery,
                             Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create a car "
                    + car, throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarRequest =
                "SELECT id, model, manufacturer_id, manufacturers.country,  manufacturer.name "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection
                        .prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet carResultSet = getCarStatement.executeQuery();
            if (carResultSet.next()) {
                car = parseCarFromResultSet(carResultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id = " + id + " from DB taxi.cars",
                    e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String getAllCarsRequest =
                "SELECT cars.id AS id, model, manufacturer_id, manufacturers.name, country "
                        + "FROM cars_drivers "
                        + "JOIN cars ON id = cars_drivers.car_id "
                        + "JOIN manufacturers ON cars.manufacturer_id "
                        + "WHERE cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllCarsStatement = connection.createStatement()) {
            ResultSet carsResultSet = getAllCarsStatement.executeQuery(getAllCarsRequest);
            while (carsResultSet.next()) {
                carList.add(parseCarFromResultSet(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from DB taxi.cars", e);
        }
        for (Car car : carList) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        boolean shouldChangeDrivers;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            shouldChangeDrivers = updateCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car by id = " + car.getId(), e);
        }
        if (shouldChangeDrivers) {
            deleteDrivers(car);
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection
                        .prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            int deleteRowsCount = deleteStatement.executeUpdate();
            return deleteRowsCount != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id = " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carList = new ArrayList<>();
        String getAllCarByDriverRequest =
                "SELECT cars.id, model, manufacturer_id, manufacturers.name, country "
                        + "FROM cars_drivers JOIN cars ON cars.id = cars_drivers.car_id "
                        + "JOIN manufacturers ON cars.manufacturer_id "
                        + "WHERE cars.is_deleted = FALSE AND cars_drivers.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection
                        .prepareStatement(getAllCarByDriverRequest)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet carsResultSet = getCarsStatement.executeQuery();
            while (carsResultSet.next()) {
                carList.add(parseCarFromResultSet(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id = " + driverId, e);
        }
        for (Car car : carList) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return carList;
    }

    private void deleteDrivers(Car car) {
        String deleteAllDriversByCarRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarDrivers = connection
                        .prepareStatement(deleteAllDriversByCarRequest)) {
            deleteCarDrivers.setLong(1, car.getId());
            deleteCarDrivers.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't deleted drivers by car " + car + " in DB cars_drivers", e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection
                        .prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car.", e);
        }
    }

    private List<Driver> getDrivers(Long id) {
        List<Driver> driverList = new ArrayList<>();
        String getManufacturerForCar =
                "SELECT * FROM cars_drivers AS cd "
                        + "JOIN drivers AS d ON cd.driver_id = d.id "
                        + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerStatement = connection
                         .prepareStatement(getManufacturerForCar)) {
            getManufacturerStatement.setLong(1, id);
            ResultSet driversResultSet = getManufacturerStatement.executeQuery();
            while (driversResultSet.next()) {
                driverList.add(parseDriverFromResultSet(driversResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get manufacturer from DB taxi.manufacturers "
                    + "by id " + id, e);
        }
        return driverList;
    }

    private Driver parseDriverFromResultSet(ResultSet driversResultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setName(driversResultSet.getString("name"));
        driver.setLicenseNumber(driversResultSet.getString("license_number"));
        driver.setId(driversResultSet.getObject("id", Long.class));
        return driver;
    }

    private Car parseCarFromResultSet(ResultSet carsResultSet) throws SQLException {
        Car car = new Car();
        car.setId(carsResultSet.getObject("id", Long.class));
        car.setModel(carsResultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(carsResultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(carsResultSet.getString("name"));
        manufacturer.setCountry(carsResultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
