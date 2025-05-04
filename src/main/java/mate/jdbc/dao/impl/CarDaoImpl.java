package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) "
                               + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertRequest,
                          Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create a car: "
                                              + car + ". ", e);
        }
        setCarToDriversRelation(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, "
                            + "m.name AS manufacturer_name, m.country, "
                            + "d.id AS driver_id, d.name AS driver_name, d.license_number "
                            + "FROM cars c "
                            + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                            + "JOIN cars_drivers cd ON c.id = cd.cars_id "
                            + "JOIN drivers d ON d.id = cd.drivers_id "
                            + "WHERE c.id = ? AND c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        Car foundedCar = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getRequest)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                foundedCar = (foundedCar == null)
                        ? buildCarFromResultSet(resultSet)
                        : addDriverToCar(buildDriverFromResultSet(resultSet), foundedCar);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by id " + id, e);
        }
        return Optional.ofNullable(foundedCar);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllRequest = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, "
                               + "m.name AS manufacturer_name, m.country,"
                               + "d.id AS driver_id, d.name AS driver_name, d.license_number "
                               + "FROM cars c "
                               + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                               + "JOIN cars_drivers cd ON c.id = cd.cars_id "
                               + "JOIN drivers d ON d.id = cd.drivers_id "
                               + "WHERE c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        Car currentCar = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                if (currentCar == null) {
                    currentCar = buildCarFromResultSet(resultSet);
                } else if (currentCar.getId().equals(resultSet.getLong("car_id"))) {
                    addDriverToCar(buildDriverFromResultSet(resultSet), currentCar);
                } else {
                    cars.add(currentCar);
                    currentCar = buildCarFromResultSet(resultSet);
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
        String updateRequest = "UPDATE cars "
                               + "SET manufacturer_id = ?, model = ? "
                               + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(updateRequest)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                                              + car + " in cars DB.", e);
        }
        deletePreviousRelations(car.getId());
        setCarToDriversRelation(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        deletePreviousRelations(id);
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Long> carsId = new ArrayList<>();
        String getAllByDriverRequest =
                "SELECT cars_id "
                + "FROM cars_drivers cd "
                + "JOIN cars c ON cd.cars_id = c.id "
                + "WHERE drivers_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement =
                         connection.prepareStatement(getAllByDriverRequest)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsId.add(resultSet.getLong(1));
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
        String insertRequest = "DELETE FROM cars_drivers "
                               + "WHERE cars_id = ? AND drivers_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, previousCarVersion.getId());
            for (Driver driver : previousCarVersion.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete "
                    + "relations between " + previousCarVersion
                    + " and drivers.", e);
        }
    }

    private Car addDriverToCar(Driver newDriver, Car foundedCar) throws SQLException {
        List<Driver> drivers = new ArrayList<>(List.copyOf(foundedCar.getDrivers()));
        drivers.add(newDriver);
        foundedCar.setDrivers(drivers);
        return foundedCar;
    }

    private Car buildCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = buildManufacturerFromResultSet(resultSet);
        Driver driver = buildDriverFromResultSet(resultSet);
        List<Driver> drivers = List.of(driver);
        return new Car(carId, model, manufacturer, drivers);
    }

    private Manufacturer buildManufacturerFromResultSet(ResultSet resultSet)
            throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("country");
        return new Manufacturer(manufacturerId, manufacturerName, country);
    }

    private Driver buildDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("driver_id", Long.class);
        String driverName = resultSet.getString("driver_name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(driverId, driverName, licenseNumber);
    }

    private void setCarToDriversRelation(Car car) {
        if (car.getDrivers().isEmpty()) {
            return;
        }
        String insertRequest = "INSERT INTO cars_drivers"
                               + " (cars_id, drivers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create relation between car with id"
                                              + car.getId() + " and drivers with id: "
                                              + car.getDrivers().stream()
                                                      .map(Driver::getId)
                                                      .collect(Collectors.toList())
                                              + ".", e);
        }
    }
}
