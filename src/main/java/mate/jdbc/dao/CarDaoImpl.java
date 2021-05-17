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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long carId) {
        String selectRequest = "SELECT c.id, m.id as m_id, model, name, country "
                + "FROM cars c JOIN manufacturers m "
                + "ON manufacturer_id = m.id "
                + "WHERE c.id = ? "
                + "AND c.deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, carId);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a car by id " + carId, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(carId));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT c.id, m.id as m_id, model, name, country "
                + "FROM cars c JOIN manufacturers m "
                + "ON manufacturer_id = m.id "
                + "WHERE c.deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(selectRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? "
                + "AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + " in DB.", e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String updateRequest = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(updateRequest)) {
            deleteCarStatement.setLong(1, carId);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectRequest = "SELECT c.id, m.id as m_id, model, name, country "
                + "FROM cars c JOIN manufacturers m "
                + "ON manufacturer_id = m.id "
                + "JOIN cars_drivers cd "
                + "ON c.id = car_id"
                + "WHERE driver_id = ? "
                + "AND c.deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverCars = connection.prepareStatement(selectRequest)) {
            getDriverCars.setLong(1, driverId);
            ResultSet resultSet = getDriverCars.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driver id: "
                    + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        Long manufacturerId = resultSet.getObject("m_id", Long.class);
        String model = resultSet.getString("model");
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturerId);
        Car car = new Car(model, manufacturer);
        car.setId(carId);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String selectRequest = "SELECT id, name, license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd ON id = driver_id "
                + "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement =
                        connection.prepareStatement(selectRequest)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers for a car with id: "
                    + carId, e);
        }
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver of a " + car, e);
        }
    }

    private void deleteDrivers(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(deleteRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            insertDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver of a " + car, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }
}
