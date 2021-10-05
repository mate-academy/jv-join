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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT car_id, model, manufacturer_id, m.name, m.country "
                + "FROM cars c  "
                + "JOIN manufacturers m "
                + "ON manufacturer_id = m.id "
                + "WHERE car_id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement = connection.prepareStatement(query)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT car_id, model, manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from taxi_db.",
                    throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ?"
                + "WHERE car_id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setObject(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in taxi_db.", throwable);
        }
        deleteRelations(car);
        createNewRelations(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement = connection.prepareStatement(query)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.car_id, model, manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON manufacturer_id = m.id "
                + "JOIN cars_drivers cd  ON c.car_id = cd.car_id "
                + " WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriversStatement = connection.prepareStatement(query)) {
            getCarsByDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriversStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars by drivers id "
                    + driverId, throwable);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement = connection.prepareStatement(query)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert  drivers to car  " + car, throwable);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long carsId) {
        String query = "SELECT d.driver_id, name, license_number"
                + " FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.driver_id = cd.driver_id "
                + "WHERE cd.car_id = ?; ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, carsId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get drivers by id " + carsId, throwable);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        String licenseNumber = resultSet.getString("license_number");
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(licenseNumber);
        driver.setId(resultSet.getObject("driver_id", Long.class));
        return driver;
    }

    void deleteRelations(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsForCarStatement
                        = connection.prepareStatement(query)) {
            deleteRelationsForCarStatement.setLong(1, car.getId());
            deleteRelationsForCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete relations for car " + car, e);
        }
    }

    void createNewRelations(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createRelationStatement = connection.prepareStatement(query)) {
            createRelationStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createRelationStatement.setLong(2, driver.getId());
                createRelationStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create relation for "
                    + car + ". ", throwable);
        }
    }
}
