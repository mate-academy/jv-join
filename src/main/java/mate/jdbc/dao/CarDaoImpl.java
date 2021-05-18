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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        boolean isCreated = false;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                isCreated = true;
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert Car " + car + " to DB", e);
        }
        if (isCreated) {
            addDriverToCar(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, m.name "
                      + "AS manufacturer_name, m.country FROM cars c "
                      + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                      + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, m.name "
                + "AS manufacturer_name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE "
                + "AND m.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars from cars table", e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                     + "WHERE id = ? AND is_deleted = FALSE;";
        int result = 0;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            result = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        if (result > 0) {
            removeDriverFromCar(car.getId());
            addDriverToCar(car);
            return car;
        }
        throw new RuntimeException("There is nothing to update");
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, m.name "
                + "AS manufacturer_name, m.country, cd.driver_id FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try {
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars by driverId= " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDrivers(car.getId()));
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) {
        try {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
            manufacturer.setName(resultSet.getString("manufacturer_name"));
            manufacturer.setCountry(resultSet.getString("country"));
            Car car = new Car();
            car.setId(resultSet.getObject("car_id", Long.class));
            car.setModel(resultSet.getString("model"));
            car.setManufacturer(manufacturer);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from resultSet", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) {
        try {
            Driver driver = new Driver();
            driver.setId(resultSet.getObject("id", Long.class));
            driver.setName(resultSet.getString("name"));
            driver.setLicenseNumber(resultSet.getString("license_number"));
            return driver;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver from resultSet", e);
        }
    }

    private List<Driver> getAllDrivers(Long carId) {
        String query = "SELECT * FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                     + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers of the car by carId= "
                                             + carId, e);
        }
    }

    private void addDriverToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver to car " + car, e);
        }
    }

    private void removeDriverFromCar(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete driver by carId= " + carId, e);
        }
    }
}
