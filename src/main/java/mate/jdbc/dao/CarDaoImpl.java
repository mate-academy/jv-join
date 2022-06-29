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
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                + car + ". ", e);
        }
        if (car.getDrivers() != null) {
            for (Driver driver : car.getDrivers()) {
                addCarDriver(car, driver);
            }
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.id,model, manufacturers.name,"
                + "manufacturers.id,manufacturers.country "
                + "FROM taxi_db.cars "
                + "INNER JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturers(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.setDrivers(getDriversByCar(car));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id,model,"
                + "manufacturers.name,manufacturers.id,manufacturers.country "
                + "FROM taxi_db.cars "
                + "INNER JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id ? "
                + "WHERE car.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturers(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCar(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        deleteCarDrivers(car);
        String query = "UPDATE cars "
                + "SET model = ?,manufacturer_id = ? "
                + "WHERE id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        for (Driver driver : car.getDrivers()) {
            addCarDriver(car, driver);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        deleteCarDrivers(get(id));
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id "
                + "FROM cars_drivers "
                + "INNER JOIN cars "
                + "ON car_id = cars.id "
                + "WHERE driver_id = ? ;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getLong("cars.id")));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars ", e);
        }
        return cars;
    }

    private void addCarDriver(Car car, Driver driver) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driver.getId());
            statement.setLong(2,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add drivers for "
                + car + ". ", e);
        }
    }

    private void deleteCarDrivers(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers for  "
                + car + ". ", e);
        }
    }

    private Car getCarWithManufacturers(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("cars.id", Long.class);
        String carModel = resultSet.getString("model");
        Long manufacturerId = resultSet.getLong("manufacturers.id");
        String manufacturerName = resultSet.getString("manufacturers.name");
        String manufacturerCountry = resultSet.getString("manufacturers.country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerName,manufacturerCountry);
        return new Car(carId,carModel,manufacturer);
    }

    private List<Driver> getDriversByCar(Car car) {
        String query = "SELECT drivers.id,name,drivers.license_number "
                + "FROM cars_drivers "
                + "INNER JOIN drivers "
                + "ON driver_id = drivers.id "
                + "WHERE car_id = ? AND drivers.is_deleted = 0;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car " + car, e);
        }
        return drivers;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getLong("drivers.id");
        String driverName = resultSet.getString("name");
        String driverLicenseNumber = resultSet.getString("drivers.license_number");
        Driver driver = new Driver(driverId,driverName,driverLicenseNumber);
        return driver;
    }
}
