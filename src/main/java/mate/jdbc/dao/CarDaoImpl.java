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
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private ManufacturerService manufacturerService;

    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement =
                        connection.prepareStatement(insertRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert car: "
                    + car.getModel() + " to DB ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? and c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement =
                        connection.prepareStatement(getRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car by id: "
                    + id + " from DB", e);
        }
        if (car != null) {
            car.setDrivers(getAllDriversByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id WHERE c.is_deleted = false;";
        List<Car> allCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllStatement = connection.createStatement()) {
            ResultSet resultSet =
                    getAllStatement.executeQuery(getAllRequest);
            while (resultSet.next()) {
                allCars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from DB", e);
        }
        allCars.forEach(car -> car.setDrivers(getAllDriversByCarId(car.getId())));
        return allCars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, "
                + "manufacturer_id = ? WHERE id = ? and is_deleted = false;";
        int updateCount;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement =
                        connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateCount = updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car by id: "
                    + car.getId() + " from DB", e);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = true where id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id: "
                    + id + " from DB", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        String carModel = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturerId);
        Car car = new Car(carModel, manufacturer);
        car.setId(carId);
        return car;
    }

    public List<Car> getCarsByDriverId(Long driverId) {
        String getCarsRequest =
                "SELECT c.id AS id, c.model AS model, m.id AS manufacturer_id, "
                + "m.name AS name, m.country AS country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ? AND d.is_deleted = FALSE;";
        List<Car> allCarsByDriver = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement =
                        connection.prepareStatement(getCarsRequest)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet =
                    getCarsStatement.executeQuery();
            while (resultSet.next()) {
                allCarsByDriver.add(getCar(resultSet));
            }
            return allCarsByDriver;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver:"
                    + driverId + " from DB", e);
        }
    }

    public List<Driver> getAllDriversByCarId(Long carId) {
        String getDriversRequest = "SELECT id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?";
        List<Driver> driverList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driverList.add(parseDriver(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all drivers by car:"
                    + carId + " from DB", e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("name"),
                resultSet.getString("license_number"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                        connection.prepareStatement(insertRequest)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t add drivers to car: " + car.getId(), e);
        }
    }

    private void deleteDrivers(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement =
                        connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete drivers by car.id: "
                    + car.getId() + " from DB", e);
        }
    }
}
