package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(insertRequest,
                             Statement.RETURN_GENERATED_KEYS)) {

            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT c.id AS car_id,model,manufacturer_id,m.name,country "
                + "FROM CARS c "
                + "JOIN MANUFACTURERS m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {

            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();

            if (resultSet.next()) {
                car = parseCarWithManufacturerResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT * FROM cars WHERE is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? and is_deleted = false;";
        String deleteOldRelationsQuery =
                "UPDATE cars_drivers SET is_deleted = true WHERE car_id = ?;";
        String insertNewRelationsQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateFieldsStatement = connection.prepareStatement(updateQuery);
                 PreparedStatement deleteOldRelationsStatement =
                        connection.prepareStatement(deleteOldRelationsQuery);
                 PreparedStatement insertNewRelationsStatement =
                        connection.prepareStatement(insertNewRelationsQuery)) {
            updateFieldsStatement.setString(1, car.getModel());
            updateFieldsStatement.setLong(2, car.getManufacturer().getId());
            updateFieldsStatement.setLong(3, car.getId());
            updateFieldsStatement.executeUpdate();

            deleteOldRelationsStatement.setLong(1, car.getId());
            deleteOldRelationsStatement.executeUpdate();

            for (Driver driver : car.getDrivers()) {
                insertNewRelationsStatement.setLong(1, car.getId());
                insertNewRelationsStatement.setLong(2, driver.getId());
                insertNewRelationsStatement.executeUpdate();
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            softDeleteCarStatement.setLong(1,id);
            int numberOfDeletedRows = softDeleteCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT c.id, model,driver_id FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByDriverStatement =
                        connection.prepareStatement(getAllByDriverRequest)) {
            getCarByDriverStatement.setLong(1,driverId);
            ResultSet resultSet = getCarByDriverStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Car parseCarWithManufacturerResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    public List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id,name,license_number FROM drivers dr "
                + "JOIN cars_drivers cd ON dr.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id,driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setDrivers(getDriversForCar(car.getId()));
        return car;
    }
}
