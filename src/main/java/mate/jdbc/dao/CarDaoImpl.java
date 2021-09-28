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
        String insertCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(insertCarQuery,
                            Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        addAllDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id, model, m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getCarQuery = "SELECT c.id, model, m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all cars", throwable);
        }
        cars.forEach(car -> car
                .setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateDriverStatement
                        = connection.prepareStatement(updateCarQuery)) {
            updateDriverStatement.setString(1, car.getModel());
            updateDriverStatement.setLong(2, car.getManufacturer().getId());
            updateDriverStatement.setLong(3, car.getId());
            updateDriverStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in driversDB.", throwable);
        }
        deleteAllDrivers(car.getId());
        addAllDrivers(car);
        return car;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String carsForDriverIdQuery = "SELECT c.id, c.model, c.manufacturer_id, "
                + "m.name, m.country FROM cars c JOIN cars_drivers cd ON cd.car_id = "
                + "c.id JOIN manufacturers m ON m.id = c.manufacturer_id WHERE cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarsForDriverStatement
                         = connection.prepareStatement(carsForDriverIdQuery)) {
            getCarsForDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsForDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can`t get cars id for Driver with id " + driverId
                + "from DB" + throwable);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car(model, manufacturer);
        Long id = resultSet.getObject("id", Long.class);
        car.setId(id);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversQueryForCarQuery = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers ca ON d.id = ca.driver_id WHERE ca.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement =
                        connection.prepareStatement(getDriversQueryForCarQuery)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all drivers "
                    + "for car with id:  " + carId + " in driversDB.", throwable);
        }
        return drivers;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }

    private boolean deleteAllDrivers(Long carId) {
        String deleteAllDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement =
                         connection.prepareStatement(deleteAllDriversQuery)) {
            getDriversForCarStatement.setLong(1, carId);
            int rowsChanged = getDriversForCarStatement.executeUpdate();
            return rowsChanged > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete all drivers "
                    + "for car with id:  " + carId + " in driversDB.", throwable);
        }
    }

    private boolean addAllDrivers(Car car) {
        String addAllDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        if (car.getDrivers() == null) {
            return true;
        }
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addAllDriversStatement =
                         connection.prepareStatement(addAllDriversQuery)) {
            addAllDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addAllDriversStatement.setLong(2, driver.getId());
                if (addAllDriversStatement.executeUpdate() < 1) {
                    return false;
                }
            }
            return true;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete all drivers "
                    + "for car with id:  " + car.getId() + " in driversDB.", throwable);
        }
    }
}
