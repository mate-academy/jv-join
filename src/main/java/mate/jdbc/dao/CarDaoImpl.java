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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                         PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKey = statement.getGeneratedKeys();
            if (generatedKey.next()) {
                car.setId(generatedKey.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car" + car, e);
        }
        insertDriversToCar(car);
        return car;
    }

    private void insertDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country"
                + " FROM cars AS c INNER JOIN manufacturers AS m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(car));
        }
        return Optional.ofNullable(car);
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long idCar = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        car.setId(idCar);
        car.setModel(model);
        Long idManufacturer = resultSet.getObject("manufacturer_id", Long.class);
        String nameManufacturer = resultSet.getString("name");
        String countryManufacturer = resultSet.getString("country");
        Manufacturer manufacturer
                = new Manufacturer(idManufacturer, nameManufacturer, countryManufacturer);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDrivers(Car car) {
        String query = "SELECT * FROM drivers AS d INNER JOIN cars_drivers AS c_d"
                + " ON c_d.driver_id = d.id "
                + "WHERE c_d.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            List<Driver> drivers = new ArrayList<>();
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long idDriver = resultSet.getObject("driver_id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                drivers.add(new Driver(idDriver, name, licenseNumber));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers of car " + car, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars AS c INNER JOIN manufacturers AS m"
                + " ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars form DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car " + car, e);
        }
        deleteDriversFromCar(car);
        insertDriversToCar(car);
        return car;
    }

    private void deleteDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from car " + car, e);
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
            throw new DataProcessingException("Can't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars AS c INNER JOIN manufacturers AS m"
                + " ON c.manufacturer_id = m.id"
                + " INNER JOIN cars_drivers AS c_d ON c.id = c_d.car_id"
                + " WHERE c_d.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car));
        }
        return cars;
    }
}
