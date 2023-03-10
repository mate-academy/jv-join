package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?)";
        List<Driver> drivers = car.getDrivers();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            insertDriverToDriverDB(drivers);
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            car.setDrivers(getDriversForCar(car.getId()));
            insertDriversToCarDriversDB(car, drivers);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.manufacturer_id, man.name, man.country, c.model "
                + "FROM cars c JOIN Manufacturers man "
                + "ON man.id = c.manufacturer_id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "SET c.model = ?, c.manufacturer_id = ?, cd.driver_id = ? "
                + "WHERE c.id = ? AND cd.car_id = ? AND c.is_deleted = FALSE";
        List<Driver> driversOfCar = car.getDrivers();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(4, car.getId());
            statement.setLong(5, car.getId());
            for (Driver driver : driversOfCar) {
                statement.setLong(3, driver.getId());
                statement.executeUpdate();
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        String getRequest = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCarsStatement = connection.prepareStatement(getRequest)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find cars in DB by driver driverId "
                    + driverId, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String request = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement = connection.prepareStatement(request)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car carId " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) {
        try {
            Long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String licenseNumber = resultSet.getString("license_number");
            return new Driver(id, name, licenseNumber);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver from DB", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getLong("manufacturer_id");
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer =
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        Long id = resultSet.getLong("id");
        String model = resultSet.getString("model");
        return new Car(id, model, manufacturer);
    }

    private void insertDriverToDriverDB (List<Driver> drivers) {
        String query = "INSERT INTO drivers (id, name, license_number) VALUES(?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            for (Driver driver : drivers) {
                statement.setLong(1, driver.getId());
                statement.setString(2, driver.getName());
                statement.setString(3, driver.getLicenseNumber());
                statement.executeUpdate();
            }
        } catch (SQLException a) {
            throw new DataProcessingException("can't insert driver to drivers table", a);
        }
    }

    private void insertDriversToCarDriversDB(Car car, List<Driver> drivers)
            throws SQLException {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            for (Driver driver : drivers) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("cant insert nothing to cars_drivers table", e);
        }
    }
}
