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
                PreparedStatement createCarStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        addCarDrivers(car.getDrivers(), car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, manufacturers.id AS manufacturer_id,"
                + " manufacturers.name AS manufacturer_name, manufacturers.country"
                + " AS manufacturer_country, model FROM cars c"
                + " JOIN manufacturers  ON c.manufacturer_id = manufacturers.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id + ". ", e);
        }
        car.setDrivers(getCarDrivers(id));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, manufacturers.id AS manufacturer_id,"
                + " manufacturers.name AS manufacturer_name,"
                + " manufacturers.country AS manufacturer_country, model "
                + "FROM cars c JOIN manufacturers  ON c.manufacturer_id = manufacturers.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        cars.forEach(car -> car.setDrivers(getCarDrivers(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
        removeDriversFromCar(car);
        addCarDrivers(car.getDrivers(), car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, manufacturer_id, model "
                + "FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id"
                + " WHERE cd.driver_id = ? AND c.is_deleted = FALSE AND "
                + "d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement
                        = connection.prepareStatement(query)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(setCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id " + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) {
        try {
            Long id = resultSet.getObject("car_id", Long.class);
            String model = resultSet.getString("model");
            Manufacturer manufacturer = new Manufacturer(
                    resultSet.getObject("manufacturer_id", Long.class),
                    resultSet.getString("manufacturer_name"),
                    resultSet.getString("manufacturer_country"));
            return new Car(id, model, manufacturer);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't parse data", e);
        }
    }

    private List<Driver> getCarDrivers(Long id) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement
                        = connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, id);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDrivers(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers for car by id " + id, e);
        }
    }

    private Driver parseDrivers(ResultSet resultSet) {
        try {
            Driver driver = new Driver(resultSet.getObject("id", Long.class),
                    resultSet.getString("name"),
                    resultSet.getString("license_number"));
            return driver;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't parse data", e);
        }
    }

    private void removeDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversFromCarStatement
                        = connection.prepareStatement(query)) {
            removeDriversFromCarStatement.setLong(1, car.getId());
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove drivers from car " + car + e);
        }
    }

    private void addCarDrivers(List<Driver> drivers, Car car) {
        String insertCarDriverSql = "INSERT INTO car_driver (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addCarDriversStatement
                        = connection.prepareStatement(insertCarDriverSql)) {
            for (Driver driver : drivers) {
                addCarDriversStatement.setLong(1, driver.getId());
                addCarDriversStatement.setLong(2, car.getId());
                addCarDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't add drivers to car", e);
        }
    }

    private Car setCar(ResultSet resultSet) {
        try {
            Car car = new Car();
            car.setId(resultSet.getObject("car_id", Long.class));
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
            car.setManufacturer(manufacturer);
            car.setModel(resultSet.getString("model"));
            car.setDrivers(getCarDrivers(car.getId()));
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't set car", e);
        }
    }
}
