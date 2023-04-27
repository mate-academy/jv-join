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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createRequest,
                           Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert car: " + car + " to DB.", e);
        }
        insertDriversToCar(car);
        return car;
    }

    private void insertDriversToCar(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarsStatement = connection
                        .prepareStatement(insertDriversRequest)) {
            addDriversToCarsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarsStatement.setLong(2, driver.getId());
                addDriversToCarsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers for car with id: " + car.getId(), e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectCarRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement selectCarStatement = connection
                        .prepareStatement(selectCarRequest)) {
            selectCarStatement.setLong(1, id);
            ResultSet resultSet = selectCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find car by id: " + id + " in DB.", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversRequest = "SELECT driver_id, name, license_number "
                + "FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection
                        .prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers for car with id: " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(driverId, name, licenseNumber);
        return driver;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId, name, country);
        car.setManufacturer(manufacturer);
        return car;
    }

    @Override
    public List<Car> getAll() {
        String selectAllCarsRequest = "SELECT c.id AS car_id, model, manufacturer_id, "
                + "name AS manufacturer_name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement selectAllCarsStatement = connection
                        .prepareStatement(selectAllCarsRequest)) {
            ResultSet resultSet = selectAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find any car from DB", e);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car: " + car, e);
        }
        deleteCarsDriversRelation(car.getId());
        insertDriversToCar(car);
        return car;
    }

    private void deleteCarsDriversRelation(Long carId) {
        String deleteCarsDriversRelationsRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement = connection
                        .prepareStatement(deleteCarsDriversRelationsRequest)) {
            deleteRelationsStatement.setLong(1, carId);
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete cars and drivers info from DB", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarStatement = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteStatement = connection
                        .prepareStatement(deleteCarStatement)) {
            softDeleteStatement.setLong(1, id);
            int numberOfDeletedRows = softDeleteStatement.executeUpdate();
            return numberOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriver = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection.prepareStatement(getCarsByDriver)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get any car by driver id: " + driverId, e);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
    }
}
