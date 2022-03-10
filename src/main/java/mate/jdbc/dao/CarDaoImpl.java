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
        String createCarQuery = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement saveCarStatement
                            = connection.prepareStatement(createCarQuery,
                            Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
        createCarDriversRelations(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, cars.model, "
                + "cars.manufacturer_id, "
                + "manufacturers.name, "
                + "manufacturers.country "
                + "FROM taxi_schema.cars "
                + "JOIN taxi_schema.manufacturers "
                + "ON taxi_schema.cars.manufacturer_id = taxi_schema.manufacturers.id "
                + "WHERE taxi_schema.cars.id = ? AND taxi_schema.cars.is_deleted = 0;";
        Optional<Car> car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getCarStatement
                            = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = Optional.of(carParser(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT "
                + "cars.id, "
                + "cars.model, "
                + "cars.manufacturer_id, "
                + "manufacturers.name, "
                + "manufacturers.country "
                + "FROM taxi_schema.cars "
                + "JOIN taxi_schema.manufacturers "
                + "ON taxi_schema.cars.manufacturer_id = taxi_schema.manufacturers.id "
                + "WHERE taxi_schema.cars.is_deleted = 0;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllCarStatement
                            = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(carParser(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all cars", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getInfoAboutDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET cars.is_deleted "
                + "= TRUE WHERE cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement deleteCarStatement
                            = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE taxi_schema.cars "
                + "SET taxi_schema.cars.manufacturer_id = ?, taxi_schema.cars.model = ? "
                + "WHERE taxi_schema.cars.id = ? AND taxi_schema.cars.is_deleted = FALSE";
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
        deleteDrivers(car.getId());
        createCarDriversRelations(car);
        return car;

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT "
                + "drivers.id, "
                + "drivers.name, "
                + "drivers.license, "
                + "cars.id, "
                + "cars.model, "
                + "manufacturers.id, "
                + "manufacturers.name, "
                + "manufacturers.country "
                + "FROM taxi_schema.cars_drivers "
                + "JOIN taxi_schema.drivers ON taxi_schema.drivers.id "
                + "= taxi_schema.cars_drivers.driver_id "
                + "JOIN taxi_schema.cars ON taxi_schema.cars.id "
                + "= taxi_schema.cars_drivers.car_id "
                + "JOIN taxi_schema.manufacturers ON taxi_schema.cars.id "
                + "= taxi_schema.manufacturers.id "
                + "WHERE taxi_schema.drivers.id = ? AND taxi_schema.drivers.is_deleted = 0";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(carParser(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars by driverId "
                    + driverId, throwable);
        }
        for (Car car: cars) {
            car.setDrivers(getInfoAboutDrivers(car.getId()));
        }
        return cars;
    }

    private List<Driver> getInfoAboutDrivers(Long id) {
        String query = "SELECT "
                + "drivers.id, "
                + "drivers.name, "
                + "drivers.license "
                + "FROM taxi_schema.cars_drivers "
                + "JOIN taxi_schema.drivers "
                + "ON taxi_schema.cars_drivers.driver_id "
                + "= taxi_schema.drivers.id "
                + "WHERE taxi_schema.cars_drivers.car_id "
                + "= ? AND taxi_schema.drivers.is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getDriversStatement
                            = connection.prepareStatement(query,
                            Statement.RETURN_GENERATED_KEYS)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get info about drivers in car with id "
                    + id + ". ", throwable);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    public boolean deleteDrivers(Long id) {
        String query = "UPDATE taxi_schema.drivers "
                + "SET taxi_schema.drivers.is_deleted = TRUE "
                + "WHERE taxi_schema.drivers.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement
                        = connection.prepareStatement(query)) {
            deleteDriversStatement.setLong(1, id);
            return deleteDriversStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    private Car carParser(ResultSet resultSet) throws SQLException {
        final Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private void createCarDriversRelations(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement saveCarDriverStatement =
                            connection.prepareStatement(query)) {
            saveCarDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                saveCarDriverStatement.setLong(2, driver.getId());
                saveCarDriverStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
    }
}
