package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String createCarRequest = "INSERT INTO cars (model, manufacturers_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(
                        createCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String getCarRequest = "SELECT cars.id, model, manufacturers.id, manufacturers.name, "
                + "manufacturers.country FROM cars "
                + "JOIN manufacturers ON cars.manufacturers_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(getCarRequest)) {
            createStatement.setLong(1, id);
            ResultSet resultSet = createStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car " + car, e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT cars.id, model, manufacturers.id, manufacturers.name, "
                + " manufacturers.country FROM cars "
                + "JOIN manufacturers ON cars.manufacturers_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = createStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getCarDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturers_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement createStatement
                        = connection.prepareStatement(updateCarRequest)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(3, car.getId());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            deleteCarDrivers(car);
            insertDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(deleteCarRequest)) {
            createStatement.setLong(1, id);
            return createStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car by id" + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriverRequest = "SELECT cars.id, model, manufacturers.id, "
                + " manufacturers.name, manufacturers.country FROM cars "
                + "JOIN manufacturers ON manufacturers.id = manufacturers_id "
                + "JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars.is_deleted = FALSE AND driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(getCarsByDriverRequest)) {
            createStatement.setLong(1, driverId);
            ResultSet resultSet = createStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getCarDrivers(car.getId()));
        }
        return cars;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers.id", Long.class));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            createStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createStatement.setLong(2, driver.getId());
                createStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, e);
        }
    }

    private List<Driver> getCarDrivers(Long carId) {
        String getAllCarDriversRequest = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE drivers.is_deleted =  FALSE AND cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(getAllCarDriversRequest)) {
            createStatement.setLong(1, carId);
            ResultSet resultSet = createStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can' t get all car drivers by car id " + carId, e);
        }

    }

    private void deleteCarDrivers(Car car) {
        String deleteCarDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement
                        = connection.prepareStatement(deleteCarDriversRequest)) {
            createStatement.setLong(1, car.getId());
            createStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations from car " + car, e);
        }
    }
}
