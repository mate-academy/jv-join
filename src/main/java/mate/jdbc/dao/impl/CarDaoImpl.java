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
    private static final String CARS_TABLE = "cars";
    private static final String DRIVERS_TABLE = "drivers";
    private static final String MANUFACTURERS_TABLE = "manufacturers";
    private static final String CARS_DRIVERS_TABLE = "cars_drivers";

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO " + CARS_TABLE
                + " (model, manufacturer_id) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car: " + car, e);
        }

        createCarsDriversRelations(car);
        return car;
    }

    private void createCarsDriversRelations(Car car) {
        String query = "INSERT INTO " + CARS_DRIVERS_TABLE
                + " (car_id, driver_id) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create relations between car: "
                    + car + " and drivers: " + car.getDrivers(), e);
        }
    }

    @Override
    public boolean update(Car car) {
        String query = "UPDATE " + CARS_TABLE
                + " SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";

        boolean isUpdated;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            isUpdated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }

        updateCarsDriversRelations(car);
        return isUpdated;
    }

    private void updateCarsDriversRelations(Car car) {
        deleteCarsDriversRelations(car);
        createCarsDriversRelations(car);
    }

    private void deleteCarsDriversRelations(Car car) {
        String query = "DELETE FROM " + CARS_DRIVERS_TABLE
                + " WHERE car_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update relations between car: "
                    + car + " and drivers: " + car.getDrivers(), e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE " + CARS_TABLE
                + " SET is_deleted = TRUE WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.*, m.id, m.name, m.country, d.id, d.name, d.license_number "
                + "FROM " + CARS_TABLE + " c "
                + "LEFT JOIN " + MANUFACTURERS_TABLE + " m ON c.manufacturer_id = m.id "
                + "LEFT JOIN " + CARS_DRIVERS_TABLE + " cd ON c.id = cd.car_id "
                + "LEFT JOIN " + DRIVERS_TABLE + " d ON cd.driver_id = d.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";

        Optional<Car> car = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = Optional.of(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id: " + id, e);
        }
        return car;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("m.name"));
        manufacturer.setCountry(resultSet.getString("m.country"));

        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        List<Driver> drivers = new ArrayList<>();
        do {
            Long driverID = resultSet.getObject("d.id", Long.class);
            String driverName = resultSet.getString("d.name");
            String driverLicenseNumber = resultSet.getString("d.license_number");
            drivers.add(new Driver(driverID, driverName, driverLicenseNumber));
        } while (resultSet.next()
                && carId.equals(resultSet.getObject("c.id", Long.class)));

        resultSet.previous();
        return new Car(carId, model, manufacturer, drivers);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.*, m.id, m.name, m.country, d.id, d.name, d.license_number "
                + "FROM " + CARS_TABLE + " c "
                + "LEFT JOIN " + MANUFACTURERS_TABLE + " m ON c.manufacturer_id = m.id "
                + "LEFT JOIN " + CARS_DRIVERS_TABLE + " cd ON c.id = cd.car_id "
                + "LEFT JOIN " + DRIVERS_TABLE + " d ON cd.driver_id = d.id "
                + "WHERE c.is_deleted = FALSE;";

        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars", e);
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.*, m.id, m.name, m.country, d.id, d.name, d.license_number "
                + "FROM " + CARS_TABLE + " c "
                + "LEFT JOIN " + MANUFACTURERS_TABLE + " m ON c.manufacturer_id = m.id "
                + "LEFT JOIN " + CARS_DRIVERS_TABLE + " cd ON c.id = cd.car_id "
                + "LEFT JOIN " + DRIVERS_TABLE + " d ON cd.driver_id = d.id "
                + "WHERE d.id = ? AND c.is_deleted = FALSE;";

        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (!cars.isEmpty() && cars.get(cars.size() - 1).getId()
                        .equals(resultSet.getObject("c.id", Long.class))) {
                    continue;
                }
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars by driver with id: "
                    + driverId, e);
        }
        return cars;
    }
}
