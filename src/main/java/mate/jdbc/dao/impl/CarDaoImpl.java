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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement createCarStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId()); // ONE-TO-MANY
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t create new car" + car, e);
        }
        insertDrivers(car); // many-to-many
        return car;
    }

    @Override
    public Car get(Long id) {
        String getCarQuery = "SELECT c.id AS car_id, c.model, " +
                "m.id AS manufacturer_id, m.name AS manufacturer_name" +
                "FROM cars c " +
                "JOIN manufacturers m ON c.manufacturer_id = m.id " +
                "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t get a car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversByCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarsQuery = "SELECT * FROM cars c WHERE c.is_deleted = FALSE;";
        Car car;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("can`t get list of cars from DB: " + cars, e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        /*  1. обновить данные про те поля, которые есть у car(по аналогии с предыд. методами)
            Как обновлять информацию, если удалился или добавился водитель?
            2. для этого, во-первых, нужно удалить все связи из таблицы cars_drivers, where car.id = car.getId();
            после того, как мы удалили все связи для конкретной машины из таблицы cars_drivers, теперь нужно
            3. добавить новые связи с таблицей cars_drivers
        */
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement = connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, carId);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can`t delete car by id" + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllCarsByDriverQuery = "SELECT id, model, manufacturer_id FROM cars c" +
                "JOIN cars_drivers cd ON c.id = cd.car_id" +
                "WHERE cd.driver_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarsByDriverStatement = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Can`t get cars for driver with id: " + driverId, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licence_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t insert driver for car: " + car, e);
        }
    }

    private List<Driver> getAllDriversByCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getAllDriversForCarQuery = "SELECT id, name, licence_number FROM drivers d" +
                "JOIN cars_drivers cd ON d.id = cd.driver_id" +
                "WHERE cd.car_id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversForCarStatement = connection.prepareStatement(getAllDriversForCarQuery)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can`t get drivers for car with id: " + carId, e);
        }
    }
}
