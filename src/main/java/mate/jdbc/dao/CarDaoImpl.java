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
        String query = "INSERT INTO cars(model, manufacturer_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(
                                query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car to DB" + car, e);
        }
        insertNewCarsDriversRow(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(query)) {
            getCarStatement.setObject(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.model, "
                + "m.id as manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from DB", e);
        }
        cars.forEach(c -> c.setDrivers(getDriversByCarId(c.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        deleteCarsDriversRow(car);
        insertNewCarsDriversRow(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setObject(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car from DB " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT "
                + "c.id, c.model, "
                + "m.id AS manufacturer_id, m.name, m.country, "
                + "d.id AS driver_id, d.name, d.license_number "
                + "FROM cars c "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "JOIN drivers d "
                + "JOIN cars_drivers cd ON cd.car_id = c.id AND cd.driver_id = d.id "
                + "WHERE d.id = ? AND c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setObject(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find cars by driver id " + driverId, e);
        }
        cars.forEach(c -> c.setDrivers(getDriversByCarId(c.getId())));
        return cars;
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String query = "SELECT "
                + "d.id, d.name, d.license_number "
                + "FROM drivers d "
                + "JOIN cars c "
                + "JOIN cars_drivers cd ON cd.car_id = c.id AND cd.driver_id = d.id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(query)) {
            getAllDriversStatement.setObject(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers from db" + carId, e);
        }
    }

    private void deleteCarsDriversRow(Car car) {
        String query = "DELETE FROM cars_drivers cd WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarsDriversStatement =
                        connection.prepareStatement(query)) {
            deleteCarsDriversStatement.setObject(1, car.getId());
            deleteCarsDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete cars and drivers by car id " + car.getId(), e);
        }
    }

    private void insertNewCarsDriversRow(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertNewCarsDriversStatement =
                        connection.prepareStatement(query)) {
            insertNewCarsDriversStatement.setObject(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertNewCarsDriversStatement.setObject(2, driver.getId());
                insertNewCarsDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't delete cars drivers by car id " + car.getId(), e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
