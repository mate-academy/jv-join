package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
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
        String createCarRequest = "INSERT INTO cars (`model`,`manufacturer_id`) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(createCarRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create new car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarRequest =
                "SELECT c.id AS car_id, model, manufacturer_id, name, country "
                        + "FROM cars c "
                        + "INNER JOIN manufacturers m ON c.manufacturer_id = m.id "
                        + "WHERE c.is_deleted = FALSE AND c.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection
                        .prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet carResultSet = getCarStatement.executeQuery();
            if (carResultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(carResultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id = " + id + " from DB taxi.cars",
                    e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String getAllCarsRequest =
                "SELECT c.id AS car_id, model, manufacturer_id, name, country "
                        + "FROM cars AS c "
                        + "INNER JOIN manufacturers m ON c.manufacturer_id = m.id "
                        + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllCarsStatement = connection.createStatement()) {
            ResultSet carsResultSet = getAllCarsStatement.executeQuery(getAllCarsRequest);
            while (carsResultSet.next()) {
                carList.add(parseCarWithManufacturerFromResultSet(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from DB taxi.cars", e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
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
            deleteDriversCar(car);
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
            int numberOfDeletedRows = deleteStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id = " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carList = new ArrayList<>();
        String getAllCarByDriverRequest =
                "SELECT tab.car_id, tab.model, manufacturer_id, m.name, m.country "
                        + "FROM manufacturers AS m "
                        + "INNER JOIN ("
                        + "SELECT * FROM cars "
                        + "INNER JOIN cars_drivers ON cars.id = cars_drivers.car_id"
                        + ") as tab ON m.id = tab.car_id;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection
                        .prepareStatement(getAllCarByDriverRequest)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet carsResultSet = getCarsStatement.executeQuery();
            while (carsResultSet.next()) {
                carList.add(parseCarWithManufacturerFromResultSet(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id = " + driverId, e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    private boolean deleteDriversCar(Car car) {
        String deleteAllDriversByCarRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection
                        .prepareStatement(deleteAllDriversByCarRequest)) {
            deleteDriversStatement.setLong(1, car.getId());
            return deleteDriversStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't deleted drivers by car" + car + "in DB cars_drivers", e);
        }
    }

    private Car insertDrivers(Car car) {
        String insertDriversRequest =
                "INSERT INTO cars_drivers (car_id,driver_id) VALUES (?, ?)";
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
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
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
            throw new DataProcessingException("Can't get manufacturer from DB taxi.manufacturers",
                    e);
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

    private Car parseCarWithManufacturerFromResultSet(ResultSet carsResultSet) throws SQLException {
        Car car = new Car();
        car.setId(carsResultSet.getObject("car_id", Long.class));
        car.setModel(carsResultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(carsResultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(carsResultSet.getString("name"));
        manufacturer.setCountry(carsResultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
