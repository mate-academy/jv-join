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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCar = connection.prepareStatement(
                        insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCar.setString(1, car.getModel());
            createCar.setLong(2, car.getManufacturer().getId());
            createCar.executeUpdate();
            ResultSet generatedKeys = createCar.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car in DB " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery =
                "INSERT INTO cars_drivers (`car_id`, `driver_id`) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarsStatement =
                        connection.prepareStatement(insertDriversQuery)) {
            addDriversToCarsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                addDriversToCarsStatement.setLong(2, driver.getId());
                addDriversToCarsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers into cars_drivers", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT c.id AS id, c.model AS model, m.id AS manufacturer_id, m.name "
                + "AS name, m.country AS country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(getQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car data from DB by id: " + id, e);
        }
        if (car != null) {
            car.setDriverList(getDriversForCar(id));
        }
        return Optional.of(car);
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            softDeleteStatement.setLong(1, id);
            int numbersOfRowsChanged = softDeleteStatement.executeUpdate();
            return numbersOfRowsChanged != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id: " + id, e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateCarFieldsQuery =
                "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateFieldsStatement =
                        connection.prepareStatement(updateCarFieldsQuery)) {
            updateFieldsStatement.setString(1, car.getModel());
            updateFieldsStatement.setLong(2, car.getManufacturer().getId());
            updateFieldsStatement.setLong(3, car.getId());
            updateFieldsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car fields: " + car, e);
        }
        deleteOldRelationsCarsToDrivers(car);
        if (car.getDriverList() != null) {
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getCarsAndManufacturersQuery =
                "SELECT * FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getCarsAndManufacturersQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get All cars", e);
        }
        for (Car car : cars) {
            car.setDriverList(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String getAllByDriverQuery = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country "
                + "FROM cars_drivers cd "
                + "JOIN cars c ON cd.car_id = c.id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE cd.driver_id = ? and c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement =
                        connection.prepareStatement(getAllByDriverQuery)) {
            getAllCarsByDriverStatement.setLong(1, id);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars for one driver by id" + id, e);
        }
        return cars;
    }

    private void deleteOldRelationsCarsToDrivers(Car car) {
        String deleteOldRelations = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement =
                        connection.prepareStatement(deleteOldRelations)) {
            deleteRelationsStatement.setLong(1, car.getId());
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car relations to drivers: " + car, e);
        }
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriverListQuery = "SELECT d.id, d.name, d.license "
                + "FROM drivers d "
                + "JOIN cars_drivers ca "
                + "ON d.id = ca.driver_id "
                + "WHERE ca.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDrivers =
                        connection.prepareStatement(getDriverListQuery)) {
            getAllDrivers.setLong(1, id);
            ResultSet resultSet = getAllDrivers.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get List of Drivers for car id: " + id, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("d.id", Long.class));
        driver.setName(resultSet.getString("d.name"));
        driver.setLicenseNumber(resultSet.getString("d.license"));
        return driver;
    }
}
