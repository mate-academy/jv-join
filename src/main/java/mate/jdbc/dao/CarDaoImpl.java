package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(query,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Could not create car: " + car, e);
        }
        if (car.getDrivers() != null) {
            insertDriversInCar(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT cars.id, model, manufacturer_id, name, country FROM cars "
                + "INNER JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? && cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Could not get car by id: " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, model, manufacturer_id, name, country FROM cars "
                + "INNER JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Could not get a list of cars",
                    throwable);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can not update car: " + car, throwable);
        }
        removeDriversFromCar(car.getId());
        insertDriversInCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can not delete car by id: " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id, c.model, m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, driverId);
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can not get car id for driver id:  " + driverId,
                    throwable);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        String manufacturerName = resultSet.getString("name");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String country = resultSet.getString("country");
        Car car = new Car(model);
        car.setId(id);
        Manufacturer manufacturer = new Manufacturer(manufacturerName, country);
        manufacturer.setId(manufacturerId);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT * FROM cars_drivers INNER JOIN drivers "
                + "ON driver_id = drivers.id WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarDriversStatement = connection.prepareStatement(query)) {
            getCarDriversStatement.setLong(1, id);
            ResultSet resultSet = getCarDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Could not get drivers by car id: " + id, throwable);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void removeDriversFromCar(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarDriversStatement = connection.prepareStatement(query)) {
            deleteCarDriversStatement.setLong(1, carId);
            deleteCarDriversStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Could not delete driver by car id: " + carId,
                    throwable);
        }
    }

    private void insertDriversInCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                createCarStatement.setLong(1, car.getId());
                createCarStatement.setLong(2, driver.getId());
                createCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not create drivers for car: " + car, e);
        }
    }
}
