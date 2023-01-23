package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String insertRequest = "INSERT INTO taxi_db.cars (model, manufacturer_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT c.id AS car_id, "
                + "c.model, "
                + "m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM taxi_db.cars c "
                + "JOIN taxi_db.manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, "
                + "name AS manufacturer_name, "
                + "country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(updateRequest)) {
            preparedStatement.setLong(3, car.getId());
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        deleteRelations(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteStatement = "UPDATE taxi_db.cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(deleteStatement)) {
            preparedStatement.setLong(1, id);
            int numberOfDeletedRows = preparedStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id" + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRquest = "SELECT d.id AS driver_id, "
                + "d.name AS driver_name, " + "c.id AS car_id, model,  "
                + "m.id AS manufacturer_id, m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM taxi_db.cars_drivers cd "
                + "JOIN taxi_db.cars c "
                + "ON cd.car_id = c.id "
                + "JOIN taxi_db.manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "JOIN taxi_db.drivers d "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarRequest = connection
                        .prepareStatement(getAllByDriverRquest)) {
            addDriverToCarRequest.setLong(1, driverId);
            ResultSet resultSet = addDriverToCarRequest.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get info by drivers id " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO taxi_db.cars_drivers "
                + "(car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarRequest = connection
                        .prepareStatement(insertDriversRequest)) {
            addDriverToCarRequest.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarRequest.setLong(2, driver.getId());
                addDriverToCarRequest.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers to car" + car, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllAuthorsForBookRequest = "SELECT id, name, license_number "
                + "FROM taxi_db.drivers d "
                + "JOIN taxi_db.cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllAuthorsStatement = connection
                        .prepareStatement(getAllAuthorsForBookRequest)) {
            getAllAuthorsStatement.setLong(1, carId);
            ResultSet resultSet = getAllAuthorsStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers by car id " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(id, name, licenseNumber);
        return driver;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private void deleteRelations(Car car) {
        String deleteOldRelations = "DELETE FROM taxi_db.cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection
                        .prepareStatement(deleteOldRelations)) {
            preparedStatement.setLong(1, car.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Cannot remove old relations in car" + car, e);
        }
    }
}
