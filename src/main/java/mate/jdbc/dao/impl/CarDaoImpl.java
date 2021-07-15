package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
                        .prepareStatement(createCarRequest, Statement.RETURN_GENERATED_KEYS)) {
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
        return null;
    }

    @Override
    public Car get(Long id) {
        Car car = null;
        String getCarRequest = "SELECT * FROM cars WHERE is_deleted = FALSE AND id = ? ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarRequest)) {
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
            car.setManufacturer(getManufacturerForCar(id));
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String getAllCarsRequest = "SELECT * FROM cars WHERE is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllCarsStatement = connection.createStatement()) {
            ResultSet carsResultSet = getAllCarsStatement.executeQuery(getAllCarsRequest);
            while (carsResultSet.next()) {
                carList.add(parseCarFromResultSet(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from DB taxi.cars", e);
        }
        if (!carList.isEmpty()) {
            for (Car car : carList) {
                car.setDrivers(getDriversForCar(car.getId()));
                car.setManufacturer(getManufacturerForCar(car.getId()));
            }
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        boolean changeDrivers;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            changeDrivers = updateCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car by id = " + car.getId(), e);
        }
        if (changeDrivers) {
            deleteDriversCar(car);
            addAllDriversToCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
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
                "SELECT c.id,c.model, c.manufacturer_id "
                        + "FROM taxi.cars_drivers cd "
                        + "JOIN cars c on cd.car_id = c.id "
                        + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
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
        setManufacturerDriversToCar(carList);
        return carList;
    }

    private void addAllDriversToCar(Car car) {
        String deleteAllDriversByCarRequest =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection
                        .prepareStatement(deleteAllDriversByCarRequest)) {
            deleteDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                deleteDriversStatement.setLong(2, driver.getId());
                deleteDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers to the car in DB cars_drivers", e);
        }
    }

    private void deleteDriversCar(Car car) {
        String deleteAllDriversByCarRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection
                        .prepareStatement(deleteAllDriversByCarRequest)) {
            deleteDriversStatement.setLong(1, car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't deleted drivers by car in DB cars_drivers", e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id,driver_id) VALUES (?, ?)";
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

    private Manufacturer parseManufacturerFromResultSet(ResultSet manufacturerResultSet)
            throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(manufacturerResultSet.getLong("id"));
        manufacturer.setName(manufacturerResultSet.getString("name"));
        manufacturer.setCountry(manufacturerResultSet.getString("country"));
        return manufacturer;
    }

    private Manufacturer getManufacturerForCar(Long id) {
        Manufacturer manufacturer = null;
        String getManufacturerRequest =
                "SELECT * FROM cars "
                        + "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.id = ? AND manufacturers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerStatement = connection
                        .prepareStatement(getManufacturerRequest)) {
            getManufacturerStatement.setLong(1, id);
            ResultSet manufacturerResultSet = getManufacturerStatement.executeQuery();
            if (manufacturerResultSet.next()) {
                manufacturer = parseManufacturerFromResultSet(manufacturerResultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get manufacturer for car by id = " + id
                    + "from DB", e);
        }
        return manufacturer;
    }

    private List<Driver> getDriversForCar(Long id) {
        List<Driver> driverList = new ArrayList<>();
        String getManufacturerForCar =
                "SELECT * FROM `cars_drivers` "
                        + "JOIN `drivers` ON cars_drivers.driver_id = drivers.id "
                        + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE;";
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
        driver.setId(driversResultSet.getLong("id"));
        return driver;
    }

    private Car parseCarFromResultSet(ResultSet carsResultSet) throws SQLException {
        Car car = new Car();
        car.setId(carsResultSet.getLong("id"));
        car.setModel(carsResultSet.getString("model"));
        return car;
    }

    private void setManufacturerDriversToCar(List<Car> carList) {
        if (!carList.isEmpty()) {
            for (Car car : carList) {
                car.setManufacturer(getManufacturerForCar(car.getId()));
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
    }
}
