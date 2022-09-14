package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO car (model, manufacturer_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car;" + car, e);
        }
        addDriverToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarById = "SELECT 'car.id', 'car.model', "
                + "'manufacturer_id', "
                + "'manufacturer.name', "
                + "'manufacturer.country' "
                + "FROM 'car' JOIN 'manufacturer'"
                + "ON car.manufacturer_id = manufacturer.id"
                + " WHERE car.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(getCarById)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getCarsRequest = "SELECT car.id, "
                + "car.model, "
                + "manufacturer.id, "
                + "manufacturer.name, "
                + "manufacturer.country "
                + "FROM car JOIN manufacturer "
                + "ON manufacturer.id = car.manufacturer_id "
                + "WHERE car.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getCarsRequest)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars from db.", e);
        }
        if (!cars.isEmpty()) {
            for (Car car:cars) {
                car.setDrivers(getDriverForCar(car.getId()));
            }
        }
        return cars;
    }

        @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE car SET model = ?, manufacturer_id = ?;";
            try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(updateCarRequest)) {
                statement.setString(1, car.getModel());
                statement.setLong(2, car.getManufacturer().getId());
                statement.setLong(3, car.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataProcessingException("Can't update car:" + car, e);
            }
            getDriverForCar(car.getId());
            addDriverToCar(car);
            return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE car SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(deleteRequest, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car by id;" + id, e);
        }
    }

    private void addDriverToCar(Car car) {
        String addDriverRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(addDriverRequest)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't added driver to car" + car, e);
        }
    }

    private void removeDriverFromCar(Long id) {
        String deleteDriverRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteDriverRequest)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete driver for car by id:" + id, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getLong("id");
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriverForCar(Long id) {
        String getDriverRequest = "SELECT driver.id, "
                + "driver.name, "
                + "driver.license_number "
                + "FROM driver "
                + "JOIN cars_drivers ON cars_drivers.driver_id = driver.id "
                + "WHERE cars_drivers.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(getDriverRequest)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriverFromResulSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver for car.id:" + id, e);
        }
    }

    private Driver getDriverFromResulSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }
}
