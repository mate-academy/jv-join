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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                        connection.prepareStatement(insertRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT cars.id as car_id, model, manufacturer_id, "
                + "name, country "
                + "FROM cars JOIN manufacturers AS m "
                + "ON cars.manufacturer_id = m.id WHERE cars.id = ? "
                + "AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(selectRequest)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't  get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, model, manufacturer_id, name, country "
                + "FROM cars AS c JOIN manufacturers AS m ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.",
                    e);
        }
        for (Car car: cars) {
            Long carId = car.getId();
            List<Driver> drivers = getDriversForCar(carId);
            car.setDrivers(drivers);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String carQuery = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement carUpdateStatement
                         = connection.prepareStatement(carQuery);
                 PreparedStatement driverUpdateStatement
                         = connection.prepareStatement(deleteQuery)) {
            carUpdateStatement.setString(1, car.getModel());
            carUpdateStatement.setLong(2, car.getManufacturer().getId());
            carUpdateStatement.setLong(3, car.getId());
            carUpdateStatement.executeUpdate();
            driverUpdateStatement.setLong(1, car.getId());
            driverUpdateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't soft delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cd.car_id FROM cars_drivers AS cd JOIN cars "
                + "ON cars.id = cd.car_id WHERE cd.driver_id = ? AND cars.is_deleted = false;";
        List<Long> carsId = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long carId = resultSet.getObject(1, Long.class);
                carsId.add(carId);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id: " + driverId, e);
        }
        List<Car> cars = new ArrayList<>();
        for (Long carId: carsId) {
            cars.add(get(carId).get());
        }
        return cars;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, e);
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
        String query = "SELECT id, name, license_number FROM drivers AS d "
                + "JOIN cars_drivers AS cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id=? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id " + carId, e);
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
