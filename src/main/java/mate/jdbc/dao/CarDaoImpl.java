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
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private ManufacturerDao manufacturerDao;
    @Inject
    private DriverDao driverDao;

    @Override
    public Car create(Car car) {
        if (car.getManufacturer().getId() == null
                || manufacturerDao.get(car.getManufacturer().getId()).isEmpty()) {
            car.getManufacturer().setId(manufacturerDao.create(car.getManufacturer()).getId());
        }
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        for (Driver driver : car.getDrivers()) {
            if (driver.getId() == null || driverDao.get(driver.getId()).isEmpty()) {
                driver.setId(driverDao.create(driver).getId());
            }
            assignDriver(car, driver);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car;
        String query = "SELECT c.id AS id, model, m.id AS man_id, name, country, m.is_deleted "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (!resultSet.next()) {
                return Optional.empty();
            }
            car = getCar(resultSet);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        car.setDrivers(getDriversList(car));
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> carsList = new ArrayList<>();
        String query = "SELECT c.id AS id, model, m.id AS man_id, name, country, m.is_deleted "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement getCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars from DB ", throwable);
        }
        for (Car car : carsList) {
            car.setDrivers(getDriversList(car));
        }
        return carsList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteOldDriversConnections(car);
        for (Driver driver : car.getDrivers()) {
            assignDriver(car, driver);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carsList = new ArrayList<>();
        String query = "SELECT c.id AS id, model, m.id AS man_id, name, country, m.is_deleted "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "RIGHT JOIN cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "WHERE c.is_deleted = FALSE and cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection.prepareStatement(query)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars from DB by driver id "
                    + driverId, throwable);
        }
        for (Car car : carsList) {
            car.setDrivers(getDriversList(car));
        }
        return carsList;
    }

    private void deleteOldDriversConnections(Car car) {
        String query = "UPDATE cars_drivers SET is_deleted = TRUE WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteConnectionStatement = connection.prepareStatement(query)) {
            deleteConnectionStatement.setLong(1, car.getId());
            deleteConnectionStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete old connections for car with id "
                    + car.getId(), throwable);
        }
    }

    private void assignDriver(Car car, Driver driver) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carDriverRelationsStatement
                        = connection.prepareStatement(query)) {
            carDriverRelationsStatement.setLong(1, car.getId());
            carDriverRelationsStatement.setLong(2, driver.getId());
            carDriverRelationsStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car-driver pair"
                    + car + ". ", throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("man_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId, name, country);
        if (resultSet.getBoolean("is_deleted")) {
            manufacturer = null;
        }
        return new Car(id, model, manufacturer);
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private List<Driver> getDriversList(Car car) {
        List<Driver> driversList = new ArrayList<>();
        String query = "SELECT driver_id, name, license_number "
                + "FROM cars_drivers "
                + "JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE car_id = ? AND cars_drivers.is_deleted = FALSE "
                + "AND drivers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            getDriversStatement.setLong(1, car.getId());
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driversList.add(getDriver(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get driver for car id "
                    + car.getId(), throwable);
        }
        return driversList;
    }
}
