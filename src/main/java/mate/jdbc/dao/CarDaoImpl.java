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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
            car.getDrivers().forEach(d -> addDriverToCar(d, car));
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversForCar(id));
        }
        return car;
    }

    private List<Driver> getAllDriversForCar(Long carId) {
        String query = "SELECT drivers.id, name, license_number "
                + "FROM drivers JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriverStatement
                         = connection.prepareStatement(query)) {
            getAllDriverStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriverStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(parseDriverFromResultSet(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of car by driver id "
                    + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver newDriver = new Driver();
        newDriver.setId(resultSet.getObject("id", Long.class));
        newDriver.setName(resultSet.getString("name"));
        newDriver.setLicenseNumber(resultSet.getString("license_number"));
        return newDriver;

    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Car newCar = new Car();
        newCar.setManufacturer(manufacturer);
        newCar.setModel(model);
        newCar.setId(id);
        return newCar;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id, manufacturers.name, "
                + "manufacturers.country FROM cars JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars",
                    throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
        removeAllDriversFromCar(car);
        for (int i = 0; i < car.getDrivers().size(); i++) {
            addDriverToCar(car.getDrivers().get(i), car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    public void addDriverToCar(Driver driver, Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addDriverToCarStatement
                        = connection.prepareStatement(query)) {
            addDriverToCarStatement.setLong(1, driver.getId());
            addDriverToCarStatement.setLong(2, car.getId());
            addDriverToCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car" + driver + car, e);
        }
    }

    public void removeAllDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement removeDriverFromCarStatement
                        = connection.prepareStatement(query)) {
            removeDriverFromCarStatement.setLong(1, car.getId());
            removeDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove driver from car" + car, e);
        }

    }

    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model FROM cars c "
                + "JOIN cars_drivers cd ON cd.car_id = c.id WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarByDriverStatement
                         = connection.prepareStatement(query)) {
            getAllCarByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarByDriverStatement.executeQuery();
            List<Car> carList = new ArrayList<>();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of car by driver id "
                    + driverId, e);
        }
    }
}
