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
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setObject(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, cars.model, manufacturers.id AS manufacturer_id, "
                + "manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers ON manufacturers.id = manufacturer_id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, "
                + "manufacturers.id AS manufacturer_id, manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers ON manufacturers.id = manufacturer_id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars.", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteDriversFromCar(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriverQuery = "SELECT cars.id, model, manufacturer_id, "
                + "manufacturers.id AS manufacturer_id, manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN cars_drivers ON cars.id = cars_drivers.cars_id "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars_drivers.drivers_id = ? AND cars.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement
                        = connection.prepareStatement(getCarsByDriverQuery)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all by driver id = "
                    + driverId, throwable);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    private void deleteDriversFromCar(Long carId) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setObject(1, carId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete "
                    + "drivers from a car with id: " + carId, e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDrivers = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverToCarStatement
                        = connection.prepareStatement(insertDrivers)) {
            insertDriverToCarStatement.setObject(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverToCarStatement.setObject(2, driver.getId());
                insertDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException exc) {
            throw new DataProcessingException("Couldn't insert drivers into a car" + car, exc);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, country);
        manufacturer.setId(manufacturerId);
        Car car = new Car(model, manufacturer);
        car.setId(id);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversByCarId = "SELECT drivers.id AS drivers_id, "
                + "drivers.name AS drivers_name, "
                + "drivers.license_number AS drivers_license_number "
                + "FROM drivers "
                + "JOIN cars_drivers ON drivers.id = drivers_id "
                + "WHERE cars_drivers.cars_id = ? AND drivers.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversByCarIdStatement
                        = connection.prepareStatement(getDriversByCarId)) {
            getDriversByCarIdStatement.setObject(1, id);
            ResultSet resultSet = getDriversByCarIdStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException exc) {
            throw new DataProcessingException("Couldn't getting drivers for car - " + id, exc);
        }
        return drivers;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("drivers_id", Long.class);
        String name = resultSet.getString("drivers_name");
        String licenseNumber = resultSet.getString("drivers_license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }
}
