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

    public static final int FIRST_INDEX = 1;
    public static final int SECOND_INDEX = 2;
    public static final int THIRD_INDEX = 3;

    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (manufacturer_id, model)\n "
                               + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(FIRST_INDEX, car.getManufacturer().getId());
            statement.setString(SECOND_INDEX, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(FIRST_INDEX, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                                              + car + ". ", e);
        }
        setRelationsCarDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, \n"
                            + "m.name AS manufacturer_name, m.country, \n"
                            + "d.id AS driver_id, d.name AS driver_name, d.license_number \n"
                            + "FROM cars c \n"
                            + "JOIN manufacturers m ON c.manufacturer_id = m.id \n"
                            + "JOIN cars_drivers cd ON c.id = cd.cars_id \n"
                            + "JOIN drivers d ON d.id = cd.drivers_id \n"
                            + "WHERE c.id = ? AND c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        Car foundedCar = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getRequest)) {
            statement.setLong(FIRST_INDEX, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                foundedCar = (foundedCar == null)
                        ? initializedNewCar(resultSet)
                        : addDriverToCar(resultSet, foundedCar);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
        return Optional.ofNullable(foundedCar);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, \n"
                               + "m.name AS manufacturer_name, m.country,\n"
                               + "d.id AS driver_id, d.name AS driver_name, d.license_number\n"
                               + "FROM cars c \n"
                               + "JOIN manufacturers m ON c.manufacturer_id = m.id \n"
                               + "JOIN cars_drivers cd ON c.id = cd.cars_id\n"
                               + "JOIN drivers d ON d.id = cd.drivers_id\n"
                               + "WHERE c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        Car currentCar = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (currentCar == null) {
                    currentCar = initializedNewCar(resultSet);
                } else if (currentCar.getId().equals(resultSet.getLong("car_id"))) {
                    addDriverToCar(resultSet, currentCar);
                } else {
                    cars.add(currentCar);
                    currentCar = initializedNewCar(resultSet);
                }
            }
            cars.add(currentCar);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars\n "
                               + "SET manufacturer_id = ?, model = ?\n "
                               + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(updateRequest)) {
            statement.setLong(FIRST_INDEX, car.getManufacturer().getId());
            statement.setString(SECOND_INDEX, car.getModel());
            statement.setLong(THIRD_INDEX, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                                              + car + " in cars DB.", e);
        }
        deletePreviousRelations(car.getId());
        setRelationsCarDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        deletePreviousRelations(id);
        String query = "UPDATE cars SET is_deleted = TRUE \nWHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(FIRST_INDEX, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Long> carsId = new ArrayList<>();
        String getAllByDriverRequest =
                "SELECT cars_id\n"
                + "FROM cars_drivers cd\n"
                + "JOIN cars c ON cd.cars_id = c.id\n"
                + "WHERE drivers_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(getAllByDriverRequest)) {
            statement.setLong(FIRST_INDEX, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsId.add(resultSet.getLong(FIRST_INDEX));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        List<Car> cars = new ArrayList<>();
        extractCarsByCarsId(carsId, cars);
        return cars;
    }

    private void extractCarsByCarsId(List<Long> carsId, List<Car> cars) {
        if (!carsId.isEmpty()) {
            for (Long carId : carsId) {
                cars.add(get(carId).orElseThrow(() ->
                        new DataProcessingException("Can't get car by id: "
                                                    + carId + " from cars DB.")));
            }
        }
    }

    private void deletePreviousRelations(Long id) {
        Car previousCarVersion = this.get(id).orElseThrow(() ->
                new DataProcessingException("Can't get existing variant of car with id: "
                                            + id + " from cars DB."));
        if (previousCarVersion.getDrivers().isEmpty()) {
            return;
        }
        String insertRequest = "DELETE FROM cars_drivers\n "
                               + "WHERE cars_id = ? AND drivers_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(FIRST_INDEX, previousCarVersion.getId());
            for (Driver driver : previousCarVersion.getDrivers()) {
                statement.setLong(SECOND_INDEX, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete "
                    + "relations between " + previousCarVersion
                    + " and drivers.", e);
        }
    }

    private Car addDriverToCar(ResultSet resultSet, Car foundedCar) throws SQLException {
        Driver newDriver = initializeDriver(resultSet);
        List<Driver> drivers = new ArrayList<>(List.copyOf(foundedCar.getDrivers()));
        drivers.add(newDriver);
        foundedCar.setDrivers(drivers);
        return foundedCar;
    }

    private Car initializedNewCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = initializeManufacturer(resultSet);
        Driver driver = initializeDriver(resultSet);
        List<Driver> drivers = List.of(driver);
        return new Car(carId, model, manufacturer, drivers);
    }

    private Manufacturer initializeManufacturer(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("country");
        return new Manufacturer(manufacturerId, manufacturerName, country);
    }

    private Driver initializeDriver(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("driver_id", Long.class);
        String driverName = resultSet.getString("driver_name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(driverId, driverName, licenseNumber);
    }

    private void setRelationsCarDrivers(Car car) {
        if (car.getDrivers().isEmpty()) {
            return;
        }
        String insertRequest = "INSERT INTO cars_drivers"
                               + " (cars_id, drivers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(FIRST_INDEX, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(SECOND_INDEX, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                                              + car + ". ", e);
        }
    }
}
