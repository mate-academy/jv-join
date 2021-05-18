package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private ManufacturerDao manufacturerDao;

    @Override
    public Car create(Car car) {
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) "
                + "values (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(createCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name AS manufacturer_name, m.country AS manufacturer_country FROM cars c "
                + "JOIN manufacturers m ON c.id = m.id WHERE c.id = ? AND c.is_deleted = FALSE ;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT c.id AS car_id, c.model AS model, m.id "
                + "AS manufacturer_id, m.name AS manufacturer_name, m.country "
                + "AS manufacturer_country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE ;";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                allCars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.",
                    throwable);
        }
        return allCars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ? "
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(updateCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.setLong(3, car.getId());
            createCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        clearDriverCarRelations(car);
        List<Driver> drivers = car.getDrivers();
        if (drivers != null) {
            for (Driver driver : drivers) {
                setDriverCarRelation(driver, car);
            }
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection
                        .prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getCarByDriver(Long driverId) {
        String findCarsQuery = "SELECT c.id AS car_id, model, manufacturer_id "
                + " FROM cars c JOIN cars_drivers cd ON c.id = cd.car_id WHERE "
                + "cd.driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection.prepareStatement(findCarsQuery)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                allCars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find cars by driver id: " + driverId, e);
        }
        return allCars;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(1, Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        Manufacturer manufacturer = manufacturerDao.get(manufacturerId).get();
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setManufacturer(manufacturer);
        car.setDrivers(getDriversForCar(id));
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversQuery = "SELECT d.id AS driver_id, d.name AS driver_name, "
                + "d.license_number AS driver_license_number FROM drivers d JOIN cars_drivers  "
                + "cd ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> allDrivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement = connection
                        .prepareStatement(getAllDriversQuery)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                allDrivers.add(getDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers by car id " + carId, e);
        }
        return allDrivers;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("driver_license_number"));
        return driver;
    }

    private void setDriverCarRelation(Driver driver, Car car) {
        String setRelationQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement setDriverCarRelationStatement = connection
                        .prepareStatement(setRelationQuery)) {
            setDriverCarRelationStatement.setLong(1, car.getId());
            setDriverCarRelationStatement.setLong(2, driver.getId());
            setDriverCarRelationStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't set driver relations with car: "
                    + car, e);
        }
    }

    private void clearDriverCarRelations(Car car) {
        String removeRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeStatement = connection
                        .prepareStatement(removeRequest)) {
            removeStatement.setLong(1, car.getId());
            removeStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't clear driver relations with car: "
                    + car, e);
        }
    }
}
