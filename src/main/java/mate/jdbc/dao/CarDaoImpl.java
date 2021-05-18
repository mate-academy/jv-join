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
            throw new DataProcessingException("Can't create "
                    + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long carId) {
        String query = "SELECT c.id as car_id, model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, carId);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get car by id " + carId, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car.getId()));
            return Optional.of(car);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get a list of cars from carsDB.",
                    throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarFieldsQuery = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        String deleteRelationsForCarIdQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            try (PreparedStatement updateCarStatement
                    = connection.prepareStatement(updateCarFieldsQuery)) {
                updateCarStatement.setString(1, car.getModel());
                updateCarStatement.setLong(2, car.getManufacturer().getId());
                updateCarStatement.setLong(3, car.getId());
                updateCarStatement.executeUpdate();
            }
            try (PreparedStatement updateCarStatement
                    = connection.prepareStatement(deleteRelationsForCarIdQuery)) {
                updateCarStatement.setLong(1, car.getId());
                updateCarStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't update "
                    + car + " in carsDB.", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, carId);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete car with id " + carId, throwable);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection.prepareStatement(query)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert a list of drivers to the car"
                    + car + ". ", throwable);
        }
    }

    private Car getCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("m.name"));
        manufacturer.setCountry(resultSet.getString("m.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String query = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND d.deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement = connection.prepareStatement(query)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get a list of drivers from driversDB.",
                    throwable);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }
}
