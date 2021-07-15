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
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                          .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String selectRequest = "SELECT c.id AS car_id, model,"
                + " m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection
                          .prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String selectAllRequest = "SELECT c.id AS car_id, model,"
                + " m.id AS manufacturer_id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection
                           .prepareStatement(selectAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB " + e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement = connection
                          .prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car in DB " + e);
        }
        removeDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement softDeleteCarStatement = connection
                         .prepareStatement(deleteRequest)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car from DB by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String selectRequest = "SELECT c.id AS car_id, c.model AS model,"
                + " m.id AS manufacturer_id, m.name AS name,"
                + " m.country AS country  FROM taxi.cars c "
                + "JOIN taxi.cars_drivers cd ON c.id = cd.car_id "
                + "JOIN taxi.manufacturers m ON c.manufacturer_id = m.id"
                + "WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement getAllDriversByDriverStatement = connection
                         .prepareStatement(selectRequest)) {
            getAllDriversByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllDriversByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB by driver id: "
                    + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        String carName = resultSet.getString("model");
        Long carId = resultSet.getObject("car_id", Long.class);
        Car car = new Car(carName, manufacturer);
        car.setId(carId);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String selectRequest = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement getAllDriversStatement = connection
                          .prepareStatement(selectRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car by id: " + carId, e);
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
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection
                        .prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers into car : " + car, e);
        }
    }

    private void removeDrivers(Long carId) {
        String removeDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement removeDriversFromCarStatement = connection
                         .prepareStatement(removeDriversRequest)) {
            removeDriversFromCarStatement.setLong(1, carId);
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove drivers from car by car_id : "
                    + carId, e);
        }
    }
}
