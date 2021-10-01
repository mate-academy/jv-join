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
        String query = "INSERT INTO `library_db`.`cars` (model, manufacture_id) VALUES (?, ?);";
        Long manufacturerId = getManufacturerId(car.getManufacturer().getName());

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setObject(2, manufacturerId);
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
    }

    private Long getManufacturerId(String name) {
        String query = "SELECT `id` FROM `library_db`.`manufacturers` WHERE name = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerIdStatement
                        = connection.prepareStatement(query)) {
            getManufacturerIdStatement.setString(1, name);
            ResultSet resultSet = getManufacturerIdStatement.executeQuery();
            Long id = null;
            if (resultSet.next()) {
                id = resultSet.getObject(1, Long.class);
            }
            return id;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get id for " + name
                    + " from manufacturers");
        }
    }

    @Override
    public Car get(Long id) {
        Car car = null;
        String query = "SELECT c.id, c.model, c.manufacture_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacture_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = FALSE;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarIdStatement = connection.prepareStatement(query)) {
            getCarIdStatement.setObject(1, id);
            ResultSet resultSet = getCarIdStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a car by id: " + id + ". ", e);
        }
        if (car == null) {
            throw new DataProcessingException("No car with this ID: " + id + ". ");
        }
        car.setDrivers(getDriversCar(id));
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT c.id, c.model, c.manufacture_id, m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m on m.id = c.manufacture_id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE";

        try (Connection connection = ConnectionUtil.getConnection();
                Statement selectAllCarStatement = connection.createStatement()) {
            ResultSet resultSet = selectAllCarStatement.executeQuery(query);
            while (resultSet.next()) {
                Car car = getCarResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars!");
        }
        for (Car car: cars) {
            List<Driver> drivers = getDriversCar(car.getId());
            car.setDrivers(drivers);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacture_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + ". ");
        }
        deleteAllDriversFromCar(car);
        for (Driver driver: car.getDrivers()) {
            addDriverToCar(driver, car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarById = connection.prepareStatement(query)) {
            deleteCarById.setObject(1, id);
            return deleteCarById.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete by car_id: "
                    + id + ". ");
        }
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUE (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverToCarStatement = connection.prepareStatement(query)) {
            insertDriverToCarStatement.setObject(1, driver.getId());
            insertDriverToCarStatement.setObject(2, car.getId());
            insertDriverToCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver_id: "
                    + driver.getId() + " and car_id: " + car.getId() + ". ");
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String query = "DELETE FROM cars_drivers WHERE driver_id = ? AND car_id = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriverFromCarStatement
                        = connection.prepareStatement(query);) {
            deleteDriverFromCarStatement.setObject(1, driver.getId());
            deleteDriverFromCarStatement.setObject(2, car.getId());
            deleteDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver_id: "
                    + driver.getId() + " and car_id: " + car.getId() + ". ");
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private Car getCarResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("c.id", Long.class));
        car.setModel(resultSet.getString("c.model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("c.manufacture_id", Long.class));
        manufacturer.setName(resultSet.getString("m.name"));
        manufacturer.setCountry(resultSet.getString("m.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversCar(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT cd.driver_id, d.name, d.license_number  FROM cars_drivers cd "
                + "JOIN drivers d on d.id = cd.driver_id WHERE car_id = ? AND d.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement selectDriversCar = connection.prepareStatement(query)) {
            selectDriversCar.setObject(1, id);
            ResultSet resultSet = selectDriversCar.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("cd.driver_id", Long.class));
                driver.setName(resultSet.getString("d.name"));
                driver.setLicenseNumber(resultSet.getString("d.license_number"));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get the List of drivers by car id: "
                    + id + ". ", e);
        }
        return drivers;
    }

    private void deleteAllDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteAllDriversFromCarStatement
                        = connection.prepareStatement(query)) {
            deleteAllDriversFromCarStatement.setObject(1, car.getId());
            deleteAllDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete all driver from the car by car_ID: "
                    + car.getId() + ". ");
        }
    }
}
