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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarPreparedStatement = connection
                     .prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarPreparedStatement.setString(1, car.getModel());
            createCarPreparedStatement.setLong(2, car.getManufacturer().getId());
            createCarPreparedStatement.executeUpdate();
            ResultSet result = createCarPreparedStatement.getGeneratedKeys();
            if (result.next()) {
                car.setId(result.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t insert to DB, car -" + car, throwables);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectQuery = "SELECT * FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarPreparedStatement = connection
                     .prepareStatement(selectQuery)) {
            getCarPreparedStatement.setLong(1, id);
            ResultSet rs = getCarPreparedStatement.executeQuery();
            if (rs.next()) {
                car = parseCarFromResultSet(rs);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllQuery = "SELECT * FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id WHERE c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCarsPreparedStatement = connection
                     .prepareStatement(getAllQuery)) {
            ResultSet rs = getAllCarsPreparedStatement.executeQuery();
            while (rs.next()) {
                cars.add(parseCarFromResultSet(rs));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all cars from DB" + throwables);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarsPreparedStatement = connection
                     .prepareStatement(updateQuery)) {
            updateCarsPreparedStatement.setString(1, car.getModel());
            updateCarsPreparedStatement.setLong(2, car.getManufacturer().getId());
            updateCarsPreparedStatement.setLong(3, car.getId());
            updateCarsPreparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t update car:" + car, throwables);
        }
        insertDrivers(car);
        removeDriver(car.getId());
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarsPreparedStatement = connection
                     .prepareStatement(deleteQuery)) {
            deleteCarsPreparedStatement.setLong(1, id);
            return deleteCarsPreparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t delete car by ID: " + id, throwables);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        List<Car> cars = new ArrayList<>();
        String byDriverQuery = "SELECT * FROM cars c JOIN taxi.cars_drivers cd"
                + " ON c.id = cd.car_idJOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement byDriverQueryPreparedStatement = connection
                     .prepareStatement(byDriverQuery)) {
            byDriverQueryPreparedStatement.setLong(1, id);
            ResultSet resultSet = byDriverQueryPreparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all cars by driver id: " + id, throwables);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement isertDriverPreparedStatement = connection
                     .prepareStatement(insertDriversQuery)) {
            isertDriverPreparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                isertDriverPreparedStatement.setLong(2, driver.getId());
                isertDriverPreparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t insert drivers to car: " + car, throwables);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet rs) {
        Driver driver = new Driver();
        try {
            driver.setId(rs.getObject("id", Long.class));
            driver.setName(rs.getString("name"));
            driver.setLicenseNumber(rs.getString("license_number"));
            return driver;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Problems with parsing driver", throwables);
        }
    }

    private Car parseCarFromResultSet(ResultSet rs) {
        Manufacturer manufacturer = new Manufacturer();
        try {
            manufacturer.setId(rs.getObject("manufacturer_id", Long.class));
            manufacturer.setName(rs.getString("name"));
            manufacturer.setCountry(rs.getString("country"));
            Car car = new Car(rs.getString("model"), manufacturer);
            car.setId(rs.getObject("car_id", Long.class));
            return car;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Problems with parsing car", throwables);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String selectRequest = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversForCarPreparedStatement = connection
                     .prepareStatement(selectRequest)) {
            getDriversForCarPreparedStatement.setLong(1, id);
            ResultSet resultSet = getDriversForCarPreparedStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get drivers by carId: " + id, throwables);
        }
    }

    private void removeDriver(Long id) {
        String removeDriverQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement removeDriversFromCarPreparedStatement = connection
                     .prepareStatement(removeDriverQuery)) {
            removeDriversFromCarPreparedStatement.setLong(1, id);
            removeDriversFromCarPreparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t remove driver by this carId: "
                    + id, throwables);
        }
    }
}
