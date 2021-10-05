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
        String insertRequest = "insert into cars (model, manufacturer_id) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection
                     .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t create car: " + car, throwables);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String getRequest = "SELECT cars.id As car_id, cars.model,"
                + "manufacturers.name AS manufacturer_name, manufacturers.id AS manufacturer_id,"
                + "manufacturers.country AS manufacturer_country,"
                + " cars.is_deleted FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }                    // mb optional
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get data by id: " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT c.id As car_id, c.model,"
                + "m.name AS manufacturer_name, "
                + "m.id AS manufacturer_id, "
                + "m.country AS manufacturer_country, "
                + "c.is_deleted "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllStatement = connection
                     .prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all cars from db", throwables);
        }
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";    // mb need to change it for car_id
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement = connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getALLCarsByDriverRequest = "SELECT c.id As car_id, c.model,"
                + "m.name AS manufacturer_name, "
                + "m.id AS manufacturer_id, "
                + "m.country AS manufacturer_country, "
                + "c.is_deleted "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd "
                + "ON cd.car_id = c.id "
                + "WHERE cd.driver_id = ? AND cars.is_deleted = FALSE";

        return null;
    }

    @Override
    public Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        Car car = new Car(id, model, manufacturer);
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversRequest = "SELECT id, name, licenseNumber"
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id"
                + "WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getDriversStatement = connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get driver by car id " + carId, throwables);
        }
    }
}
