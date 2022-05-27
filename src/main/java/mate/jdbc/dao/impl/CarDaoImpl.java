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
        String query = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car. " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, c.model, "
                + "c.manufacturer_id, m.name, m.country\n"
                + "FROM cars c\n"
                + "JOIN manufacturers m ON m.id = c.manufacturer_id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, c.model, c.manufacturer_id, m.name, m.country\n"
                + "FROM cars c\n"
                + "JOIN manufacturers m ON m.id = c.manufacturer_id\n"
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarAllWithDrivers(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars "
                    + "from cars table.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
        deleteDriversFromCar(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String sqlGetAllDrivers = "SELECT c.id as car_id, c.model,"
                + " m.id as manufacturer_id, m.name, m.country,"
                + " d.id as driver_id, d.name as driver_name, d.license_number "
                + " FROM drivers d"
                + "         JOIN cars_drivers cd on d.id = cd.driver_id"
                + "         JOIN cars c on c.id = cd.car_id"
                + "         JOIN manufacturers m on m.id = c.manufacturer_id"
                + " WHERE d.is_deleted = FALSE AND c.is_deleted = FALSE"
                + " AND d.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(sqlGetAllDrivers)) {
            statement.setLong(1, driverId);
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getAllCarByDriverId(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars "
                    + "from cars table.", e);
        }
    }

    @Override
    public void addDriverToCar(Driver driverIns, Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            addDriversStatement.setLong(1, car.getId());
            addDriversStatement.setLong(2, driverIns.getId());
            addDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can`t insert drivers to car: "
                    + car.getId() + " driverId, " + driverIns.getId(), e);

        }
    }

    public void removeDriverFromCar(Driver driverDel, Car car) {
        String deleteRelationsQuery = "DELETE FROM cars_drivers cd WHERE cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteRelationsQuery)) {
            for (Driver driver : car.getDrivers()) {
                if (driverDel.equals(driver)) {
                    statement.setLong(1, driver.getId());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t relation cars_drivers carId:" + car.getId()
                    + ", driverId; " + driverDel.getId(), e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            addDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversStatement.setLong(2, driver.getId());
                addDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t insert drivers to car: " + car, e);
        }
    }

    private void deleteDriversFromCar(Car car) {
        String deleteRelationsQuery = "DELETE FROM cars_drivers cd WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteRelationsQuery)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can`t deleted relations cars_drivers", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(getManufacturer(resultSet));
        return car;
    }

    private Car getCarAllWithDrivers(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(getManufacturer(resultSet));
        car.setDrivers(getDriversByCarId(resultSet.getObject("car_id", Long.class)));
        return car;
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String getAllDrivers = "SELECT d.id as driver_id, d.name, d.license_number, c.model\n"
                + "FROM drivers d\n"
                + "JOIN cars_drivers cd on d.id = cd.driver_id\n"
                + "JOIN cars c ON cd.car_id = c.id\n"
                + "WHERE cd.car_id = ? ";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(getAllDrivers)) {
            statement.setLong(1, carId);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers "
                    + "from drivers table.", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car getAllCarByDriverId(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = getManufacturer(resultSet);
        List<Driver> drivers = getDrivers(resultSet);
        return new Car(carId, model, manufacturer, drivers);
    }

    private List<Driver> getDrivers(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return List.of(driver);
    }
}
