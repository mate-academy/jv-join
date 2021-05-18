package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?)";
        boolean created = false;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement = connection.prepareStatement(query,
                         Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                created = true;
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        if (created) {
            insertDriversRelations(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id AS car_id, model, "
                + "m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id + " ",
                    throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(car.getId()));
            return Optional.of(car);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, "
                + "m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturer(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of drivers from driversDB.",
                    throwable);
        }
        setDriversToListOfDrivers(cars);
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model, m.id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ? "
                + "AND c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsByDriverStatement
                         = connection.prepareStatement(query)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of drivers from driversDB.",
                    throwable);
        }
        setDriversToListOfDrivers(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement
                         = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        removeAllDriversRelations(car);
        insertDriversRelations(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    private void setDriversToListOfDrivers(List<Car> cars) {
        if (cars.size() != 0) {
            IntStream.range(0, cars.size())
                    .forEach(i -> cars.get(i).setDrivers(getDriversByCarId(cars.get(i).getId())));
        }
    }

    private void removeAllDriversRelations(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                         connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't remove drivers relations "
                    + car, throwable);
        }
    }

    private void insertDriversRelations(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement
                        = connection.prepareStatement(query)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, throwable);
        }
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("id", Long.class);
        Manufacturer manufacturer = new Manufacturer(resultSet.getString("name"),
                resultSet.getString("country"));
        manufacturer.setId(manufacturerId);
        Long carId = resultSet.getObject("car_id", Long.class);
        String carModel = resultSet.getString("model");
        Car car = new Car();
        car.setModel(carModel);
        car.setId(carId);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String getAllAuthorsForBookQuery = "SELECT * FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement
                         = connection.prepareStatement(getAllAuthorsForBookQuery)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversWithResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't find drivers in DB by car id "
                    + carId, throwable);
        }
    }

    private Driver parseDriversWithResultSet(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }
}
