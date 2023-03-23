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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id AS id, model, manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars AS c JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement
                        = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllByCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectAllRequest = "SELECT c.id, model, manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars AS c JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCars
                        = connection.prepareStatement(selectAllRequest)) {
            ResultSet resultSet = getAllCars.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of Cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getAllByCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars AS c SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car from carsDB.", e);
        }
        deleteCarDrivers(car);
        updateCarDrivers(car);
        return car;
    }

    private void updateCarDrivers(Car car) {
        Long carId = car.getId();
        List<Driver> drivers = car.getDrivers();
        if (car.getDrivers().size() == 0) {
            return;
        }
        String deleteRequest = "INSERT INTO taxi_service.cars_drivers (`car_id`, `driver_id`) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteRequest)) {
            statement.setLong(1, car.getId());
            for (Driver driver : drivers) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers by car id" + car.getId(), e);
        }
    }

    private void deleteCarDrivers(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteRequest)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers by car id" + car.getId(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement
                        = connection.prepareStatement(deleteCarRequest)) {
            softDeleteCarStatement.setLong(1, id);
            int numberOfDeletedRows = softDeleteCarStatement.executeUpdate();
            return numberOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverRequest = "SELECT c.id AS id, model, manufacturer_id, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE c.is_deleted = FALSE AND driver_id = ? "
                + "AND d.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsByDriverRequest)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by driver id " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getAllByCar(car.getId())));
        return cars;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private List<Driver> getAllByCar(Long carId) {
        String getAllCarsByDriverRequest = "SELECT id, name, licenseNumber "
                + "FROM cars_drivers cd JOIN drivers d "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsByDriverRequest)) {
            getAllCarsStatement.setLong(1, carId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by driver id " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }
}
