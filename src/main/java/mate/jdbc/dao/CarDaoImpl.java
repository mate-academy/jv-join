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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(
                        query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + " ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c LEFT JOIN manufacturers m "
                + "on c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id + " ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id as car_id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        // Updating car's fields
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car + " ", e);
        }

        // Deleting relations from cars_drivers
        String deleteCarsDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarsDriversStatement
                        = connection.prepareStatement(deleteCarsDriversQuery)) {
            deleteCarsDriversStatement.setLong(1, car.getId());
            deleteCarsDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car drivers for " + car + " ", e);
        }

        // Creating new relations in cars_drivers
        if (car.getDrivers() != null) {
            for (Driver driver : car.getDrivers()) {
                String addCarsDriversRelationQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                        + "VALUES (?, ?)";
                try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement addCarsDriversRelationStatement
                                = connection.prepareStatement(addCarsDriversRelationQuery)) {
                    addCarsDriversRelationStatement.setLong(1, car.getId());
                    addCarsDriversRelationStatement.setLong(2, driver.getId());
                    addCarsDriversRelationStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new DataProcessingException("Couldn't add drivers for " + car + " ", e);
                }
            }
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
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id + " ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id as car_id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars_drivers cd "
                + "INNER JOIN cars c ON cd.car_id = c.id "
                + "     INNER JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement
                        = connection.prepareStatement(query)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver with id "
                    + driverId + " ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT d.id, d.name, d.license_number "
                + "FROM cars_drivers cd JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE car_id = ? AND d.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversByCarIdStatement
                        = connection.prepareStatement(query)) {
            getDriversByCarIdStatement.setLong(1, carId);
            ResultSet resultSet = getDriversByCarIdStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for car with id "
                    + carId + " ", e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));
        car.setManufacturer(parseManufacturer(resultSet));
        return car;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(3, Long.class));
        manufacturer.setName(resultSet.getString(4));
        manufacturer.setCountry(resultSet.getString(5));
        return manufacturer;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject(1, Long.class));
        driver.setName(resultSet.getString(2));
        driver.setLicenseNumber(resultSet.getString(3));
        return driver;
    }
}
