package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(insertRequest,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet generateKeys = statement.getGeneratedKeys();
            if (generateKeys.next()) {
                Long id = generateKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert car to DB", e);
        }
        insertDriver(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id AS car_id, model, "
                + "m.id AS manufacturer_id, m.name, m.country\n"
                + "FROM cars AS c\n"
                + "JOIN manufacturers AS m\n"
                + "ON c.manufacturer_id = m.id\n"
                + "WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement((getQuery))) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find car in DB by id:" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as id, c.model, m.id as manufacturer_id, "
                + "m.name as manufacturer_name, m.country, "
                + "d.id as driver_id, d.name, d.license_number\n"
                + "FROM cars c\n"
                + "JOIN cars_drivers cd ON c.id = cd.car_id\n"
                + "JOIN drivers d ON d.id = cd.driver_id\n"
                + "JOIN manufacturers m ON m.id = c.manufacturer_id\n"
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            Map<Long, Car> cars = new HashMap<>();
            while (resultSet.next()) {
                Long carId = resultSet.getLong("id");
                if (resultSet.wasNull()) {
                    continue;
                }
                Car car = cars.get(carId);
                if (car == null) {
                    car = new Car();
                    car.setId(carId);
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setId(resultSet.getLong("manufacturer_id"));
                    manufacturer.setName(resultSet.getString("manufacturer_name"));
                    manufacturer.setCountry(resultSet.getString("country"));
                    car.setManufacturer(manufacturer);
                    car.setModel(resultSet.getString("model"));
                    cars.put(carId, car);
                }
                List<Driver> drivers = car.getDrivers();
                if (drivers == null) {
                    drivers = new ArrayList<>();
                    car.setDrivers(drivers);
                }
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return new ArrayList<>(cars.values());
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars from cars table", e);
        }
    }

    @Override
    public Car update(Car car) {
        String queryUpdateCar = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryUpdateCar)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.setObject(3, car.getId());
            statement.executeUpdate();
            String queryDeleteCarsDrivers = "DELETE FROM cars_drivers WHERE car_id = ?;";
            try (PreparedStatement deleteStatement =
                    connection.prepareStatement(queryDeleteCarsDrivers)) {
                deleteStatement.setLong(1, car.getId());
                deleteStatement.executeUpdate();
            }
            String queryInsertCarsDrivers = "INSERT INTO cars_drivers (car_id, driver_id) "
                    + "VALUES (?, ?);";
            try (PreparedStatement insertStatement =
                    connection.prepareStatement(queryInsertCarsDrivers)) {
                for (Driver driver : car.getDrivers()) {
                    insertStatement.setLong(1, car.getId());
                    insertStatement.setLong(2, driver.getId());
                    insertStatement.executeUpdate();
                }
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with id = " + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(deleteCarQuery)) {
            statement.setLong(1, carId);
            int numbersOfDeletedRows = statement.executeUpdate();
            return numbersOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id: " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, m.id as manufacturer_id, m.name, m.country\n"
                + "FROM cars c\n"
                + "JOIN cars_drivers cd ON c.id = cd.car_id\n"
                + "JOIN drivers d ON d.id = cd.driver_id\n"
                + "JOIN manufacturers m ON m.id = c.manufacturer_id\n"
                + "WHERE d.id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                car.setId(resultSet.getLong("id"));
                car.setModel(resultSet.getString("model"));
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
                car.setManufacturer(manufacturer);
                car.setDrivers(getDriversForCar(car.getId()));
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertDriver(Car car) {
        String insertAuthorsQuery = "INSERT INTO cars_drivers (car_id, driver_id)"
                + "VAlUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(insertAuthorsQuery)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert driver to car: " + car, e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
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

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number \n"
                + "FROM drivers AS d \n"
                + "JOIN cars_drivers AS cd \n"
                + "ON d.id = cd.driver_id \n"
                + "WHERE cd.car_id = ? \n"
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find driver for car:" + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
