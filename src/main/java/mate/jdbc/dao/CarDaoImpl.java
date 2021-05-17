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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertCarStatement = connection
                         .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setLong(2, car.getManufacturer().getId());
            insertCarStatement.executeUpdate();
            ResultSet generatedKeys = insertCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't create car " + car, e);
        }
        setDriversToACarInDB(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id, model, manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection
                        .prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car from DB by Id = " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversToACarFromDB(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT c.id, model, manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON manufacturer_id = m.id "
                + "WHERE c.is_deleted = false;";
        List<Car> cars = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(selectRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars form DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversToACarFromDB(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car " + car, e);
        }
        deleteDrivers(car);
        setDriversToACarInDB(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String softDeleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteStatement = connection
                        .prepareStatement(softDeleteRequest)) {
            softDeleteStatement.setLong(1, id);
            return softDeleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car from DB by Id = " + id, e);
        }
    }

    private void setDriversToACarInDB(Car car) {
        String insertDriverRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection
                        .prepareStatement(insertDriverRequest)) {
            insertStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(2, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't add driver of car " + car, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("c.id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("m.name"));
        manufacturer.setCountry(resultSet.getString("m.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversToACarFromDB(Car car) {
        String selectRequest = "SELECT car_id, driver_id, d.name, d.license_number "
                + "FROM cars_drivers cd "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE car_id = ? AND d.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversOfCarStatement = connection
                        .prepareStatement(selectRequest)) {
            getDriversOfCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getDriversOfCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers of car " + car, e);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("d.name"));
        driver.setLicenseNumber(resultSet.getString("d.license_number"));
        return driver;
    }

    private void deleteDrivers(Car car) {
        String deleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?; ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection
                        .prepareStatement(deleteDriversRequest)) {
            deleteDriversStatement.setLong(1, car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete drivers of car " + car, e);
        }
    }
}
