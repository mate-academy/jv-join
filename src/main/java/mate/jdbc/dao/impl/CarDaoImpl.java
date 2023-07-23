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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car "
                    + car + ". ", e);
        }
        insertDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String queryGetCar = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, m.name AS "
                + "manufacturer_name, m.country AS manufacturer_country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryGetCar)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car by id " + id + ". ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversList(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String queryGetAllCars = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, m.name AS "
                + "manufacturer_name, m.country AS manufacturer_country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryGetAllCars);) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars", e);
        }
        if (!carList.isEmpty()) {
            for (Car car : carList) {
                car.setDrivers(getDriversList(car.getId()));
            }
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String queryUpdateCar = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        int resultUpdate;
        boolean resultDelete = false;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryUpdateCar)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            resultUpdate = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car " + car + ". ", e);
        }
        deleteDriversForUpdateCar(car);
        updateDriverForUpdateCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String queryDeleteCar = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryDeleteCar)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String queryListCarsByDriver = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country, cd.driver_id "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id JOIN cars_drivers "
                + "cd ON c.id = cd.car_id WHERE driver_id = ?;";
        List<Car> carsByDriverID = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryListCarsByDriver)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsByDriverID.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars by driver id : "
                    + driverId, e);
        }
        for (Car car : carsByDriverID) {
            car.setDrivers(getDriversList(car.getId()));
        }
        return carsByDriverID;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private List<Driver> getDriversList(Long id) {
        String queryGetDrivers = "SELECT id, name, license_number FROM drivers d JOIN "
                + "cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryGetDrivers)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of drivers "
                    + "by car with id " + id + ". ", e);
        }
    }

    private void insertDriversToCar(Car car) {
        String queryAddDriversToCar = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryAddDriversToCar)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers of the car "
                    + car + ". ", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void deleteDriversForUpdateCar(Car car) {
        String queryDeleteDrivers = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryDeleteDrivers)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations by car id: "
                    + car.getId(), e);
        }
    }

    private void updateDriverForUpdateCar(Car car) {
        String queryUpdateDrivers = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryUpdateDrivers)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Can't update drivers for car with id: "
                    + car.getId(), e);
        }
    }
}
