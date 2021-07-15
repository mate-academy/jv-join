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
        String query = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement =
                         connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create car: " + car, throwable);
        }
        return insertDrivers(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, model, m.id as manufacturer_id, name, country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get a list of cars from DB.", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getCarDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String query = "SELECT c.id as car_id, m.id as manufacturer_id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsByDriverStatement =
                         connection.prepareStatement(query)) {
            getAllCarsByDriverStatement.setLong(1, id);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars by driver id: " + id, e);
        }
        for (Car car : cars) {
            car.setDrivers(getCarDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't update car: " + car + " in DB.", throwable);
        }
        return deleteDriversFromCar(car) ? insertDrivers(car) : new Car();

    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete car with id " + id, throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getCarDrivers(Long id) {
        String query = "SELECT * FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversStatement =
                         connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't find drivers in DB by car id "
                    + id, throwable);
        }
    }

    private Car insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriversStatement = connection.prepareStatement(query)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, throwable);
        }
    }

    private boolean deleteDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement removeDriverFromCarStatement =
                         connection.prepareStatement(query)) {
            removeDriverFromCarStatement.setLong(1, car.getId());
            removeDriverFromCarStatement.executeUpdate();
            return true;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete drivers from car: " + car,
                    throwable);
        }
    }
}
