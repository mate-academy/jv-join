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
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement createCarStatement =
                           connection.prepareStatement(createCarQuery,
                                   Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create a car "
                    + car, throwable);
        }
        addDriverToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarByIdQuery = "SELECT id, model, name, manufacturer_id, country "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturer_id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement getCarByIdStatement =
                           connection.prepareStatement(getCarByIdQuery)) {
            getCarByIdStatement.setLong(1, id);
            getCarByIdStatement.executeQuery();
            ResultSet resultSet = getCarByIdStatement.getResultSet();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car from DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT cars.id AS car_id, model, name, manufacturers.id "
                + "AS manufacturer_id, country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement getAllCarsStatement =
                           connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from database", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement updateCarStatement =
                           connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car in DB: " + car, e);
        }
        deleteDriverFromCar(car);
        addDriverToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarByIdQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement deleteCarByIdStatement =
                           connection.prepareStatement(deleteCarByIdQuery)) {
            deleteCarByIdStatement.setLong(1, id);
            int updatedRows = deleteCarByIdStatement.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car from DB by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery =
                "SELECT cars.id, model, manufacturer_id, manufacturer.name, country "
                + "FROM cars_drivers JOIN cars ON cars.id = cars_drivers.car_id "
                + "JOIN manufacturers ON cars.manufacturer_id "
                + "WHERE cars.is_deleted = FALSE AND cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement getAllByDriverStatement =
                           connection.prepareStatement(getAllByDriverQuery)) {
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from a DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    private Car addDriverToCar(Car car) {
        String addDriverToCarQuery = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement addDriverToCarStatement =
                           connection.prepareStatement(addDriverToCarQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers to car: " + car, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturerId);
        Long carId = resultSet.getObject("car_id", Long.class);
        String carModel = resultSet.getString("model");
        Car car = new Car(carModel, manufacturer);
        car.setId(carId);
        return car;
    }

    private List<Driver> getDriversByCarId(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getDriversByCarIdQuery = "SELECT id, name, license_number "
                + "FROM cars_drivers "
                + "JOIN drivers ON cars_drivers.driver_id = drivers.id "
                + "WHERE car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement getDriversByCarIdStatement =
                           connection.prepareStatement(getDriversByCarIdQuery)) {
            getDriversByCarIdStatement.setLong(1, carId);
            ResultSet resultSet = getDriversByCarIdStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by car id: " + carId, e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private boolean deleteDriverFromCar(Car car) {
        String deleteDriverQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement deleteDriverStatement =
                           connection.prepareStatement(deleteDriverQuery)) {
            deleteDriverStatement.setLong(1, car.getId());
            return deleteDriverStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete driver fom car " + car, e);
        }
    }
}
