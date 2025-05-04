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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1,car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1,Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ",e);
        }
        for (Driver driver : car.getDrivers()) {
            pairCarDriver(car.getId(),driver.getId());
        }
        car.setDrivers(getDriversByCarId(car.getId()));
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars "
                + "INNER JOIN manufacturers  "
                + "ON cars.manufacturer_id = manufacturer.id"
                + " AND cars.id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            car.setDrivers(getDriversByCarId(car.getId()));
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id,e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars "
                + "INNER JOIN manufacturers  "
                + "ON cars.manufacturer_id = manufacturers.id"
                + " AND cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.",e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1,car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car.toString() + " in table cars.",e);
        }
        updateCarDriversList(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars with id " + id,e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturers.id"));
        manufacturer.setName(resultSet.getString("manufacturers.name"));
        manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        Long id = resultSet.getObject("cars.id",Long.class);
        String model = resultSet.getString("cars.model");
        Car car = new Car();
        car.setId(id);
        car.setManufacturer(manufacturer);
        car.setModel(model);
        car.setDrivers(new ArrayList<Driver>());
        return car;
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String query = "SELECT * FROM drivers JOIN cars_drivers "
                + "WHERE drivers.id = cars_drivers.driver_id "
                + "AND cars_drivers.car_id = ? "
                + " AND drivers.is_deleted = FALSE";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers for carId:"
                    + carId.toString(),e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("drivers.id",Long.class));
        driver.setName(resultSet.getObject("drivers.name",String.class));
        driver.setLicenseNumber(resultSet.getObject("drivers.license_number",String.class));
        return driver;
    }

    private boolean pairCarDriver(Long carId,Long driverId) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,carId);
            statement.setLong(2,driverId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't pair car:" + carId + " driver with id "
                    + driverId,e);
        }
    }

    private boolean updateCarDriversList(Car car) {
        String query = "DELETE FROM cars_drivers where car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,car.getId());
            for (Driver driver : car.getDrivers()) {
                pairCarDriver(car.getId(),driver.getId());
            }
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove "
                    + car.toString() + " in table cars_drivers.",e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars JOIN manufacturers JOIN cars_drivers "
                + "WHERE cars.id = cars_drivers.car_id "
                + "AND cars.manufacturer_id = manufacturers.id "
                + "AND cars_drivers.driver_id = ? "
                + " AND cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars for driver:"
                    + driverId,e);
        }
        for (Car car: cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }
}

