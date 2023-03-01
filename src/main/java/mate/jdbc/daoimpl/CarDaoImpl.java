package mate.jdbc.daoimpl;

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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                createCarStatement = connection.prepareStatement(insertRequest,
                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String selectRequest = "SELECT c.id AS car_id, m.id AS manufacturer_id, model,"
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars AS c INNER JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversById(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars AS c INNER JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                getAllCarsStatement = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversById(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                UpdateCarStatement = connection.prepareStatement(query)) {
            UpdateCarStatement.setObject(1, car.getManufacturer().getId());
            UpdateCarStatement.setString(2, car.getModel());
            UpdateCarStatement.setLong(3, car.getId());
            UpdateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carDB.", e);
        }
        removeDriverFromCar(car.getId());
        insertDriverToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars set is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                deleteCarStatement = connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllByDriverRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars AS c INNER JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                + "INNER JOIN cars_drivers AS cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                getAllByDriverStatement = connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversById(car.getId()));
            }
        }
        return cars;
    }

    private void insertDriverToCar(Car car) {
        String insertDriverToCarQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                insertDriverToCarStatement = connection.prepareStatement(insertDriverToCarQuery)) {
            for (Driver driver : car.getDrivers()) {
                insertDriverToCarStatement.setLong(1, car.getId());
                insertDriverToCarStatement.setLong(2, driver.getId());
                insertDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car: " + car, e);
        }
    }

    private void removeDriverFromCar(Long carId) {
        String removeDriverFromCarQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                removeDriverFromCarStatement =
                     connection.prepareStatement(removeDriverFromCarQuery)) {
            removeDriverFromCarStatement.setLong(1, carId);
            removeDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver: " + carId, e);
        }
    }

    private List<Driver> getDriversById(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getAllDriversById = "SELECT d.id AS driver_id, d.name, d.license_number "
                + "FROM drivers AS d INNER JOIN cars_drivers AS cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement
                getDriversByIdStatement = connection.prepareStatement(getAllDriversById)) {
            getDriversByIdStatement.setLong(1, carId);
            ResultSet resultSet = getDriversByIdStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers car with id: " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String manufacturerCountry = resultSet.getString("manufacturer_country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturerId);
        Car car = new Car(model, manufacturer, null);
        car.setId(carId);
        return car;
    }
}
