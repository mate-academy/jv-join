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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generateKeys = createCarStatement.getGeneratedKeys();
            if (generateKeys.next()) {
                Long id = generateKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't insert car to DB ", throwable);
        }
        addDriverToCar(car);
        return car;
    }

    private void addDriverToCar(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement = connection
                        .prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver: car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't add driver to car ", throwable);
        }
    }

    private void removeDriverFromCar(Car car) {
        String deleteDriver = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement removeDriverStatement = connection
                         .prepareStatement(deleteDriver)) {
            removeDriverStatement.setLong(1, car.getId());
            removeDriverStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't delete driver from car: ", throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name, m.country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection
                         .prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't find car in DB by id ", throwable);
        }
        if (car != null) {
            car.setDrivers(getDriverFromCar(id));
        }
        return Optional.ofNullable(car);
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

    private List<Driver> getDriverFromCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement = connection
                         .prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't find drivers in DB by car_id " + carId, throwable);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject(1, Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    @Override
    public List<Car> getAll() {
        String selectGetAllCarQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name, m.country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarStatement = connection
                         .prepareStatement(selectGetAllCarQuery)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't find all cars in DB: ", throwable);
        }
        for (Car car: cars) {
            if (car != null) {
                car.setDrivers(getDriverFromCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
            removeDriverFromCar(car);
            addDriverToCar(car);
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection
                        .prepareStatement(deleteCarQuery)) {
            preparedStatement.setLong(1, id);
            int numberOfDeletedRows = preparedStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't delete car with id: " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id "
                + "FROM cars c "
                + "JOIN drivers d ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? ADN c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllByDriverStatement = connection
                         .prepareStatement(selectRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
            return cars;
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't find all car in DB by driver_id: ", throwable);
        }
    }
}
