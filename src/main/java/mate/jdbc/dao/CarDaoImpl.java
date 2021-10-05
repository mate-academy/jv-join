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
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        for (Driver driver : car.getDrivers()) {
            addCarsDriversRelation(car.getId(), driver.getId());
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars.id = ?";
        Optional<Car> car = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement = connection.prepareStatement(query)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = Optional.of(getCarFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get Car by id " + id, throwable);
        }
        car.ifPresent(value -> value.setDrivers(getDriversByCarId(id)));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id AS car_id, model, manufacturer_id, name, country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all cars", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update car " + car, throwable);
        }
        deleteCarsDriversRelations(car.getId());
        for (Driver driver : car.getDrivers()) {
            addCarsDriversRelation(car.getId(), driver.getId());
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, model, "
                + "manufacturer_id, m.name AS name, country "
                + "FROM cars c "
                + "JOIN manufacturers m on c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd on c.id = cd.car_id "
                + "JOIN drivers d on d.id = cd.driver_id "
                + "WHERE c.is_deleted = FALSE AND d.id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    private void addCarsDriversRelation(Long carId, Long driverId) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id)"
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarsDriversRelationStatement =
                        connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarsDriversRelationStatement.setLong(1, carId);
            saveCarsDriversRelationStatement.setLong(2, driverId);
            saveCarsDriversRelationStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create cars - drivers relation "
                    + "with carId = " + carId + " and driverId = " + driverId, throwable);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) {
        Car car = new Car();
        try {
            car.setId(resultSet.getObject("car_id", Long.class));
            car.setModel(resultSet.getString("model"));
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
            manufacturer.setName(resultSet.getString("name"));
            manufacturer.setCountry(resultSet.getString("country"));
            car.setManufacturer(manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car from resultSet "
                    + resultSet, e);
        }
        return car;
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String query = "SELECT drivers.id AS driver_id, name, license_number "
                + "FROM cars_drivers JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ? AND is_deleted = FALSE";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAlRelatedDriversStatement =
                        connection.prepareStatement(query)) {
            getAlRelatedDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAlRelatedDriversStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = getDriverFromResultSet(resultSet);
                drivers.add(driver);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get drivers by carId = "
                    + carId, throwable);
        }
        return drivers;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) {
        Driver driver = new Driver();
        try {
            driver.setId(resultSet.getObject("driver_id", Long.class));
            driver.setName(resultSet.getString("name"));
            driver.setLicenseNumber(resultSet.getString("license_number"));
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Driver from resultSet " + resultSet, e);
        }
        return driver;
    }

    private void deleteCarsDriversRelations(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarsDriversRelationStatement =
                        connection.prepareStatement(query)) {
            deleteCarsDriversRelationStatement.setLong(1, carId);
            deleteCarsDriversRelationStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete all car - drivers "
                    + "relation by car_id = " + carId, throwable);
        }
    }
}
