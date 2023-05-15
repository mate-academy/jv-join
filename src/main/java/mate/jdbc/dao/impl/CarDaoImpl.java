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
        String insertCarQuery = "INSERT INTO cars(manufacturer_id, model) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarStatement = connection
                        .prepareStatement(insertCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setLong(1, car.getManufacturer().getId());
            insertCarStatement.setString(2, car.getModel());
            insertCarStatement.executeUpdate();
            ResultSet resultSet = insertCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't insert car " + car, exception);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id AS car_id, c.model, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            Manufacturer manufacturer = null;
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't get car by id " + id, exception);
        }
        car.setDrivers(getDriversFromCar(id));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getCarsQuery = "SELECT c.id AS car_id, c.model, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getCarsQuery);) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't get all cars!", exception);
        }
        for (Car value : cars) {
            value.setDrivers(getDriversFromCar(value.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET manufacturer_id = ?, model = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't update car: " + car, exception);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String softDeleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement =
                        connection.prepareStatement(softDeleteCarQuery)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() > 0;
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't delete car with id: " + id, exception);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarByDriverQuery = "SELECT c.id AS car_id, c.model, "
                + "m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN cars_drivers cd ON c.id = cd.car_id "
                + "INNER JOIN manufacturers m ON c.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> carsList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByDriver =
                        connection.prepareStatement(getCarByDriverQuery);) {
            getCarByDriver.setLong(1, driverId);
            ResultSet resultSet = getCarByDriver.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't get car by driver ", exception);
        }
        for (Car car: carsList) {
            car.setDrivers(getDriversFromCar(car.getId()));
        }
        return carsList;
    }

    private void insertDrivers(Car car) {
        String insertDriverQuery = "INSERT INTO cars_drivers(driver_id, car_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriverStatement =
                         connection.prepareStatement(insertDriverQuery)) {
            insertDriverStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(1, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException exception) {
            throw new DataProcessingException("Can't insert driver for the car " + car, exception);
        }
    }

    private List<Driver> getDriversFromCar(Long id) {
        String getDriverByCarQuery = "SELECT id, name, license_number "
                + "FROM drivers d "
                + "INNER JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriverByCarStatement =
                        connection.prepareStatement(getDriverByCarQuery);) {
            getDriverByCarStatement.setLong(1, id);
            ResultSet resultSet = getDriverByCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't get driver by car id " + id, exception);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number")
        );
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country")
        );

        return new Car(
                resultSet.getObject("car_id", Long.class),
                manufacturer,
                resultSet.getString("model")
        );
    }

    private void deleteDrivers(Car car) {
        String deleteCarQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement =
                         connection.prepareStatement(deleteCarQuery);) {
            deleteCarStatement.setLong(1, car.getId());
            deleteCarStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataProcessingException("Couldn't delete driver for the car "
                    + car, exception);
        }
    }
}
