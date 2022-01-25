package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturers_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection.prepareStatement(createCarQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car.", throwable);
        }
    }

    @Override
    public Car get(Long id) {
        String selectCarQuery = "SELECT c.id, model, m.id as manufacturers_id, m.name, " +
                "m.country FROM cars c JOIN manufacturers m " +
                "on c.manufacturers_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(selectCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriverFromCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String selectAllCarQuery = "SELECT * FROM cars c JOIN manufacturers m on c.manufacturers_id = m.id WHERE c.is_deleted = FALSE;";

        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement getCarStatement = connection.prepareStatement(selectAllCarQuery)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            List<Car> allCarsList = new ArrayList<>();
            while (resultSet.next()) {
                allCarsList.add(parseCarFromResultSet(resultSet));
            }
            return allCarsList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all cars", throwable);
        }
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement
                     = connection.prepareStatement(deleteQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }



    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("m.name"));
        manufacturer.setCountry(resultSet.getString("m.country"));

        car.setManufacturer(manufacturer);
        return car;
    }
    
    private List<Driver> getDriverFromCar(Long carId) {
        String selectDriverQuery = "SELECT id, name, license_number FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id  WHERE cd.car_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement getDriverStatement = connection.prepareStatement(selectDriverQuery)) {
            getDriverStatement.setLong(1, carId);
            ResultSet resultSet = getDriverStatement.executeQuery();

            List<Driver> carDrivers = new ArrayList<>();
            while (resultSet.next()) {
                carDrivers.add(parseDriverFromResultSet(resultSet));
            }
            return carDrivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't find driver by driver ID " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
