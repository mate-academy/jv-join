package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
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
        String createRequest = "INSERT INTO cars(name, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement =
                        connection.prepareStatement(createRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getName());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cant insert car into DB " + car, e);
        }
        return addDriversToCar(car, car.getDrivers());
    }

    @Override
    public Optional<Car> get(Long id) {
        String gerCarByIdRequest = "SELECT c.id as car_id, c.name as car_name,"
                + " m.id as manufacturer_id, m.name as manufacturer_name,"
                + " m.country as manufacturer_country"
                + " FROM cars c JOIN manufacturers m on m.id = c.manufacturer_id"
                + " WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement =
                        connection.prepareStatement(gerCarByIdRequest)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversByCar(car));
            return Optional.of(car);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT c.id as car_id, c.name as car_name,"
                + " m.id as manufacturer_id, m.name as manufacturer_name,"
                + " m.country as manufacturer_country"
                + " FROM cars c JOIN manufacturers m on m.id = c.manufacturer_id"
                + " WHERE c.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversByCar(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET name = ?, manufacturer_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getName());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cant update car " + car, e);
        }
        deleteAllDriversByCar(car);
        addDriversToCar(car, car.getDrivers());
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteByIdRequest = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteByIdStatement =
                        connection.prepareStatement(deleteByIdRequest)) {
            deleteByIdStatement.setLong(1, id);
            int updatedRows = deleteByIdStatement.executeUpdate();
            return updatedRows >= 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant delete data with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllCarsByDriver(Driver driver) {
        String getAllCarsRequestByDriverRequest = "SELECT c.id as car_id, c.name as car_name,"
                + " m.id as manufacturer_id, m.name as manufacturer_name,"
                + " m.country as manufacturer_country"
                + " FROM cars c"
                + " JOIN manufacturers m on m.id = c.manufacturer_id"
                + " JOIN cars_drivers cd on c.id = cd.cars_id"
                + " WHERE c.is_deleted = false AND cd.drivers_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsRequestByDriverStatement =
                        connection.prepareStatement(getAllCarsRequestByDriverRequest)) {
            getAllCarsRequestByDriverStatement.setLong(1, driver.getId());
            ResultSet resultSet = getAllCarsRequestByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get all cars by driver " + driver, e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllDriversByCar(car));
        }
        return cars;
    }

    private Car addDriversToCar(Car car, List<Driver> drivers) {
        String addDriversToCarRequest = "INSERT INTO cars_drivers (cars_id, drivers_id)"
                + " VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                        connection.prepareStatement(addDriversToCarRequest)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : drivers) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant add drivers " + drivers
            + " to car " + car, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String manufacturerCountry = resultSet.getString("manufacturer_country");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(manufacturerId);
        manufacturer.setName(manufacturerName);
        manufacturer.setCountry(manufacturerCountry);
        Long carId = resultSet.getObject("car_id", Long.class);
        String carName = resultSet.getString("car_name");
        Car car = new Car();
        car.setId(carId);
        car.setName(carName);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getAllDriversByCar(Car car) {
        String getAllDriversByCarRequest = "SELECT d.id, d.name, d.license_number"
                + " FROM cars_drivers cd"
                + " JOIN drivers d on d.id = cd.drivers_id WHERE cd.cars_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversByCarStatement =
                        connection.prepareStatement(getAllDriversByCarRequest)) {
            getAllDriversByCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getAllDriversByCarStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant get drivers from car " + car, e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private boolean deleteAllDriversByCar(Car car) {
        String deleteAllDriversByIdRequest = "DELETE FROM cars_drivers WHERE cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteAllDriversByCarStatement =
                        connection.prepareStatement(deleteAllDriversByIdRequest)) {
            deleteAllDriversByCarStatement.setLong(1, car.getId());
            int updatedRows = deleteAllDriversByCarStatement.executeUpdate();
            return updatedRows >= 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant delete all drivers by car " + car, e);
        }
    }
}
