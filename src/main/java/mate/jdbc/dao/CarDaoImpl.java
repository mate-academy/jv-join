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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
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
        if (car.getDrivers() != null && !car.getDrivers().isEmpty()) {
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id,  model, manufacturer_id, name, country "
                    + "FROM cars c JOIN manufacturers m"
                    + " ON c.manufacturer_id = m.id"
                    + " WHERE c.id = ? AND c.is_deleted = false ";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1,id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get carr by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getAllDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id,  model, manufacturer_id, name, country "
                    + "FROM cars c JOIN manufacturers m"
                    + " ON c.manufacturer_id = m.id"
                    + " WHERE c.is_deleted = false ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCars = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCars.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't get a list of cars from carsDB.", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                    + " WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't update car " + car, throwable);
        }
        deleteDrivers(car);
        insertDrivers(car);
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
            throw new DataProcessingException("Couldn't delete driver with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, model, manufacturer_id, "
                    + "m.name, m.country FROM cars c"
                    + " JOIN manufacturers m ON c.manufacturer_id = m.id"
                    + " JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't get cars by driver id " + driverId, throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversForCar(car.getId()));
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        String model = resultSet.getString("model");
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Long id = resultSet.getObject("id", Long.class);
        Car car = new Car(model, manufacturer);
        car.setId(id);
        return car;
    }

    private List<Driver> getAllDriversForCar(Long carId) {
        String query = "SELECT d.id, name, license_number FROM drivers d join"
                    + " cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement = connection.prepareStatement(query)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't find drivers in DB by car id " + carId, throwable);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getLong("id"));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCar = connection.prepareStatement(query)) {
            addDriverToCar.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCar.setLong(2, driver.getId());
                addDriverToCar.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't insert driver to car " + car, throwable);
        }
    }

    private void deleteDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriverFromCar = connection.prepareStatement(query)) {
            deleteDriverFromCar.setLong(1, car.getId());
            deleteDriverFromCar.executeUpdate();
        } catch (SQLException throwable) {
            throw new RuntimeException("Can't insert driver to car " + car, throwable);
        }
    }
}
