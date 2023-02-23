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
            throw new DataProcessingException("Couldn't create "
                + car + ". ", e);
        }
        addDriversToCar(car);

        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query =
                "SELECT cars.id AS car_id, model, country, manufacturer_id, name  FROM cars "
                + "INNER JOIN manufacturers AS m ON cars.manufacturer_id = m.id "
                + "WHERE cars.id=? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = createCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get Car by id. id = "
                + id + ". ", e);
        }

        if (car != null) {
            setDriversToCarList(List.of(car));
        }

        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query =
                "SELECT cars.id AS car_id, model, country, manufacturer_id, name FROM cars "
                + "INNER JOIN manufacturers AS m ON cars.manufacturer_id = m.id "
                + "WHERE cars.is_deleted = false";
        List<Car> cars = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars", e);
        }
        setDriversToCarList(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query =
                "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                + car + " in carsDB.", e);
        }

        cleanDriverListByCarId(car.getId());
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query =
                "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id = "
                + id + " in carsDB.", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query =
                "SELECT cars.id AS car_id, model, country, manufacturer_id, m.name AS name "
                + "FROM cars_drivers cd "
                + "INNER JOIN cars ON cd.car_id = cars.id "
                + "INNER JOIN manufacturers m ON cars.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND cars.is_deleted = FALSE";

        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(createCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driverId. driverId ="
                    + driverId + ". ", e);
        }
        setDriversToCarList(cars);
        return cars;
    }

    private int cleanDriverListByCarId(Long carId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't clean list of drivers by carId = "
                + carId + ". ", e);
        }
    }

    private List<Car> setDriversToCarList(List<Car> cars) {
        String query =
                "SELECT id, name, license_number FROM drivers AS d "
                + "INNER JOIN cars_drivers AS cd ON d.id = cd.driver_id "
                + "WHERE car_id = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Car car : cars) {
                List<Driver> drivers = new ArrayList<>();
                statement.setLong(1, car.getId());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    drivers.add(new Driver(
                            resultSet.getObject("id", Long.class),
                            resultSet.getString("name"),
                            resultSet.getString("license_number")
                    ));
                }
                car.setDrivers(drivers);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a lists of drivers. ", e);
        }
        return cars;
    }

    private Car createCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country")
        ));
        return car;
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement = connection.prepareStatement(query)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add the drivers to the car. " + car, e);
        }
    }
}
