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
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        for (int i = 0; i < car.getDrivers().size(); i++) {
            addDriverToCar(car.getDrivers().get(i), car);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.id AS car_id, cars.model AS model, "
                + "manufacturers.id AS manufacturer_id, "
                + "manufacturers.country AS country, manufacturers.name AS manufacturer_name "
                + "FROM cars INNER JOIN manufacturers "
                + "ON manufacturers.id = cars.manufacturer_id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        long carId;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = carCreator(resultSet);
            }
            carId = resultSet.getObject("car_id", Long.class);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.setDrivers(getAllByCar(carId));
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT cars.id AS car_id, cars.model AS model, "
                + "manufacturers.id AS manufacturer_id, "
                + "manufacturers.country AS country, manufacturers.name manufacturer_name "
                + "FROM cars INNER JOIN manufacturers "
                + "ON manufacturers.id = cars.manufacturer_id "
                + "WHERE cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(carCreator(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.", e);
        }
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).setDrivers(getAllByCar(cars.get(i).getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            for (int i = 0; i < car.getDrivers().size(); i++) {
                addDriverToCar(car.getDrivers().get(i), car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car.getId(), e);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            List<Driver> drivers = getAllByCar(id);
            for (int i = 0; i < drivers.size(); i++) {
                removeDriverFromCar(drivers.get(i), get(id));
            }
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver with id " + id, e);
        }
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, car.getId());
            statement.setObject(2, driver.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver "
                    + driver.getId() + "to car " + car.getId() + ". ", e);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ? AND driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.setLong(2, driver.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver " + driver.getId()
                    + " from car " + car.getId(), e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT * FROM cars_drivers WHERE driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = get(resultSet.getObject("car_id", Long.class));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driver "
                    + driverId, e);
        }
        return cars;
    }

    private List<Driver> getAllByCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        DriverDao driverDao = new DriverDaoImpl();
        String query = "SELECT * FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = driverDao.get(resultSet.getObject("driver_id", Long.class)).get();
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all drivers by car "
                    + carId, e);
        }
        return drivers;
    }

    private Car carCreator(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
