package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                 PreparedStatement statement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id as car_id, c.model as car_model, m.id as"
                + " manufacturer_id, m.name as manufacturer_name, m.country as "
                + "manufacturer_country FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id  where c.id = ? and c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                         connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id: " + id, e);
        }
        if (car != null) {
            car.setDriverList(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model, "
                + "c.manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from db", e);
        }
        for (Car car : cars) {
            car.setDriverList(getDriverForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car: " + car, e);
        }
        deleteDriver(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars set is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement =
                         connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car from DB" + id + " .", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars using driver`s id"
                    + driverId + " .", e);
        }
        for (Car car : carList) {
            car.setDriverList(getDriverForCar(car.getId()));
        }
        return carList;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addDriverToCarStatement
                         = connection.prepareStatement(query)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers " + car, e);
        }
    }

    private List<Driver> getDriverForCar(Long carId) {
        String query = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement
                         = connection.prepareStatement(query)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers for car " + carId + " .", e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("car_id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void deleteDriver(Long id) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers", e);
        }
    }
}
