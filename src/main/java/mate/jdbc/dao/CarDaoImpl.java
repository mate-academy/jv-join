package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO car (model, manufacturers_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection(); PreparedStatement preparedStatement
                = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setObject(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().isPresent() ?
                    car.getManufacturer().get().getId() :
                    Optional.ofNullable(car.getManufacturer()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            insertDriver(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id_car, c.model,m.id, m.name, m.country FROM car c " +
                "JOIN manufacturers m ON c.id_car = m.id " +
                "WHERE c.id_car = ? AND c.is_delete = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = parsingCar(resultSet);
            }
            if (car != null) {
                car.setDriverList(selectDriver(id));
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id_car, c.model, m.id, m.name, m.country " +
                "FROM car c JOIN manufacturers m ON c.id_car = m.id WHERE is_delete = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Car> carList = new ArrayList<>();
            Car car = null;
            while (resultSet.next()) {
                car = parsingCar(resultSet);
            }
            if (car != null) {
                car.setDriverList(selectDriver(car.getId()));
            }
            carList.add(car);
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of Car "
                    + "from car table. ", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE car SET model = ?, manufacturers_id = ? " +
                "WHERE id_car = ? AND is_delete = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setObject(2, car.getManufacturer().isPresent() ?
                    car.getManufacturer().get().getId() :
                    Optional.ofNullable(car.getManufacturer()));
            preparedStatement.setObject(3, car.getId());
            preparedStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE car SET is_delete = TRUE WHERE id_car = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setObject(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id_car, c.model, c.manufacturers_id " +
                "FROM car c JOIN car_drivers cd " +
                "ON c.id_car = cd.driver_id WHERE cd.driver_id = ? AND c.is_delete = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Car> carList = new ArrayList<>();
            Car car = null;
            while (resultSet.next()) {
                car = new Car();
                car.setId(resultSet.getObject("id_car", Long.class));
                car.setModel(resultSet.getString("model"));
                carList.add(car);
            }
            return carList;
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't get a list of Car "
                    + "from drivers table. ", e);
        }


    }

    Car parsingCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id_car", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(java.util.Optional.of(manufacturer));
        return car;
    }

    private List<Driver> selectDriver(Long id) {
        String query = "SELECT d.id, d.name, d.license_number " +
                "FROM drivers d JOIN car_drivers cd " +
                "ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.is_deleted = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setObject(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            Driver driver = null;
            while (resultSet.next()) {
                driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                driverList.add(driver);
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of car "
                    + "from car table. ", e);
        }
    }

    private void insertDriver(Car car) {
        String query = "INSERT INTO car_drivers (car_id,driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                preparedStatement.setObject(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(" Can't insert driver to car " + car, e);
        }
    }
}
