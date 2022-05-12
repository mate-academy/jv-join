package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create a car:" + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver drivers : car.getDrivers()) {
                statement.setLong(2, drivers.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert driver to car: " + car, e);
        }
    }

    @Override
      public Car get(Long id) {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars AS c JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false AND m.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car by id:" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers for car by id:" + id, e);
        }
        return drivers;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars AS c JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false AND m.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get list of cars", e);
        }
        addDriversToCarList(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car: " + car, e);
        }
        deleteRelationInTableCarsDrivers(car);
        insertDrivers(car);
        return car;
    }

    private void deleteRelationInTableCarsDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete relation "
                    + "in table cars_drivers by car: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars AS c JOIN cars_drivers AS cd "
                + "ON c.id = cd.car_id "
                + "WHERE driver_id = ? AND c.is_deleted = false;";
        List<Car> carsByDriver = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                car.setId(resultSet.getObject("id", Long.class));
                car.setModel(resultSet.getString("model"));
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
                car.setManufacturer(manufacturer);
                carsByDriver.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver id: " + driverId, e);
        }
        addDriversToCarList(carsByDriver);
        addInfoAboutManufacturer(carsByDriver);
        return carsByDriver;
    }

    private void addInfoAboutManufacturer(List<Car> cars) {
        String query = "SELECT name, country FROM manufacturers "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Car oneCar : cars) {
                statement.setLong(1, oneCar.getManufacturer().getId());
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    Manufacturer manufacturer = oneCar.getManufacturer();
                    manufacturer.setName(resultSet.getString("name"));
                    manufacturer.setCountry(resultSet.getString("country"));
                    oneCar.setManufacturer(manufacturer);
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get manufacturer for cars: " + cars, e);
        }
    }

    private void addDriversToCarList(List<Car> cars) {
        if (!cars.isEmpty()) {
            for (Car oneCar : cars) {
                oneCar.setDrivers(getDriversForCar(oneCar.getId()));
            }
        }
    }
}
