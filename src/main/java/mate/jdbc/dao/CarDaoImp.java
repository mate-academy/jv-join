package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImp implements CarDao {
    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(insertRequest,
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
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT cars.id as car_id, model, "
                + "manufacturers.id as manufacturer_id, manufacturers.name, "
                + "manufacturers.country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? "
                + "AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
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
        String query = "SELECT cars.id as car_id, model, "
                + "manufacturers.id as manufacturer_id, manufacturers.name, "
                + "manufacturers.country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "AND cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        return cars.stream()
                .peek(car -> car.setDrivers(getDriversForCar(car.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateDriverStatement
                        = connection.prepareStatement(query)) {
            updateDriverStatement.setString(1, car.getModel());
            updateDriverStatement.setLong(2, car.getManufacturer().getId());
            updateDriverStatement.setLong(3, car.getId());
            updateDriverStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in driversDB.", throwable);
        }
        deleteId(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeletedCarStatement = connection
                        .prepareStatement(deleteCarQuery)) {
            softDeletedCarStatement.setLong(1, carId);
            return softDeletedCarStatement.executeUpdate() > 1;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectRequest = "SELECT cars.id as car_id, model, "
                + "manufacturers.id as manufacturer_id, "
                + "manufacturers.name, "
                + "manufacturers.country FROM cars "
                + "JOIN cars_drivers "
                + "ON cars_drivers.car_id = cars.id "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars_drivers.driver_id = ? "
                + "AND cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(selectRequest)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars with driver_id: "
                    + driverId, throwable);
        }
        return cars.stream()
                .peek(car -> car.setDrivers(getDriversForCar(car.getId())))
                .collect(Collectors.toList());
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet. getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDrivers = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement = connection
                        .prepareStatement(getAllDrivers)) {
            getAllDriversStatement.setLong(1,carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverStatement = connection
                        .prepareStatement(insertDriversQuery)) {
            addDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverStatement.setLong(2, driver.getId());
                addDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, e);
        }
    }

    private void deleteId(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deletedConnectionStatement = connection.prepareStatement(query)) {
            deletedConnectionStatement.setLong(1, car.getId());
            deletedConnectionStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from car: " + car, e);
        }
    }
}
