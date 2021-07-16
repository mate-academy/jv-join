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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create a car " + car, e);
        }
        insertAllCarsDriversRelations(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, model, manufacturer_id, name, country FROM cars"
                + " JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id + " from DB", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, model, manufacturer_id, name, country FROM cars"
                + " JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.is_deleted = FALSE ;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        boolean changedCar;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            changedCar = updateCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car
                    + " by id " + car.getId(), e);
        }
        if (changedCar) {
            deleteAllCarsDriversRelations(car);
            insertAllCarsDriversRelations(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement = connection.prepareStatement(query)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id " + id + " from DB", e);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, model, manufacturer_id, name, country FROM cars"
                + " JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " JOIN cars_drivers ON cars.id = cars_drivers.car_id"
                + " WHERE cars_drivers.driver_id = ? AND is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        String model = resultSet.getString("model");
        Manufacturer manufacturer = parseManufacturer(resultSet);
        Car car = new Car(model, manufacturer);
        car.setId(resultSet.getObject("cars.id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String query = "SELECT id, name, license_number FROM cars_drivers cd "
                + "JOIN drivers ON cd.driver_id = drivers.id "
                + "WHERE cd.car_id = ? AND drivers.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers for car by id " + id, e);
        }
        return drivers;
    }

    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(id);
        return manufacturer;
    }

    private void deleteAllCarsDriversRelations(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relation from DB by car " + car, e);
        }
    }

    private void insertAllCarsDriversRelations(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(query)) {
            insertStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertStatement.setLong(1, driver.getId());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert relation into DB", e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("name"),
                resultSet.getString("license_number"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }
}
