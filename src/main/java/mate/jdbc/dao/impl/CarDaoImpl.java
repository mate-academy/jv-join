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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create car: " + car, e);
        }
        insertDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, c.model AS car_model,"
                + " m.id AS manufacturer_id, m.name AS manufacturer_name,"
                + " m.country As manufacturer_country"
                + " FROM cars c"
                + " INNER JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car by id: " + id, e);
        }

        if (car != null) {
            car.setDrivers(getDriversList(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model AS car_model,"
                + " m.id AS manufacturer_id, m.name AS manufacturer_name,"
                + " m.country As manufacturer_country"
                + " FROM cars c"
                + " INNER JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement =
                        connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get a list of cars from cars table", e);
        }
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                car.setDrivers(getDriversList(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update a car: " + car, e);
        }
        removeDriversFromCar(car.getId());
        insertDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model AS car_model,"
                + " m.id AS manufacturer_id, m.name AS manufacturer_name,"
                + " m.country AS manufacturer_country"
                + " FROM cars c"
                + " INNER JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " INNER JOIN cars_drivers cd"
                + " ON c.id = cd.car_id"
                + " WHERE driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarByDriverStatement =
                        connection.prepareStatement(query)) {
            getAllCarByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver with id: "
                    + driverId, e);
        }
        if (!cars.isEmpty()) {
            for (Car car : cars) {
                car.setDrivers(getDriversList(car.getId()));
            }
        }
        return cars;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(1, Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString("car_model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void insertDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers(driver_id, car_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement =
                        connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(1, driver.getId());
                insertDriverStatement.setLong(2, car.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert drivers to car" + car, e);
        }
    }

    private void removeDriversFromCar(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversStatement =
                        connection.prepareStatement(query)) {
            removeDriversStatement.setLong(1, carId);
            removeDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t remove drivers from the car", e);
        }
    }

    private List<Driver> getDriversList(Long carId) {
        String query = "SELECT d.id AS driver_id, d.name AS driver_name,"
                + " d.license_number AS driver_license_number"
                + " FROM drivers d"
                + " INNER JOIN cars_drivers cd"
                + " ON d.id = cd.driver_id"
                + " WHERE cd.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverStatement =
                        connection.prepareStatement(query)) {
            getDriverStatement.setLong(1, carId);
            ResultSet resultSet = getDriverStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers from DB", e);
        }
        return drivers;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject(1, Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("driver_license_number"));
        return driver;
    }
}
