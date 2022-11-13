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
        String query = "INSERT INTO cars (`model`, `manufacturers_id`) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement = connection.prepareStatement(query,
                            Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car, e);
        }
        return insertDriversToCar(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT model, car_id, name, country, manufacturers_id "
                + "FROM cars c "
                + "INNER JOIN cars_drivers cd "
                + "ON cd.car_id = c.id "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturers_id = m.id "
                + "WHERE car_id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithoutListOfDrivers(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        Optional<Car> optionalCar = Optional.ofNullable(car);
        optionalCar.ifPresent(i -> i.setDrivers(getDriverList(i.getId())));
        return optionalCar;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT model, car_id, name, country, manufacturers_id, driver_id "
                        + "FROM cars c "
                        + "INNER JOIN cars_drivers cd "
                        + "ON cd.car_id = c.id "
                        + "INNER JOIN manufacturers m "
                        + "ON c.manufacturers_id = m.id "
                        + "WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                         PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithoutListOfDrivers(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriverList(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturers_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement statement
                             = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        resetCarsDriversRelations(car.getId());
        return insertDriversToCar(car);
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                         PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT model, car_id, name, country, manufacturers_id "
                + "FROM cars c "
                + "INNER JOIN cars_drivers cd "
                + "ON cd.car_id = c.id "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturers_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                         PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithoutListOfDrivers(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars for driver "
                            + "with id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriverList(car.getId()));
        }
        return cars;
    }

    private Car getCarWithoutListOfDrivers(ResultSet resultSet) throws SQLException {
        Long carID = resultSet.getObject("car_id", Long.class);
        String carModel = resultSet.getString("model");
        Manufacturer carManufacturer = getManufacturer(resultSet);
        return new Car(carModel, carManufacturer, null, carID);

    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturers_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        return new Manufacturer(id, name, country);
    }

    private List<Driver> getDriverList(Long id) {
        String query = "SELECT driver_id, name, license_number "
                + "FROM cars_drivers cd "
                + "INNER JOIN drivers d "
                + "ON cd.driver_id = d.id "
                + "WHERE car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of "
                    + "driver car with id =  " + id, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private Car insertDriversToCar(Car car) {
        Long id = car.getId();
        String query = String.format("INSERT INTO cars_drivers "
                + "(`car_id`, `driver_id`) VALUES (%d, ?)", id);
        try (Connection connection = ConnectionUtil.getConnection();
                         PreparedStatement statement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert driver to car " + car, e);
        }
        return car;
    }

    private void resetCarsDriversRelations(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                         PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars from "
                    + "cars_drivers with car_id " + carId, e);
        }
    }
}
