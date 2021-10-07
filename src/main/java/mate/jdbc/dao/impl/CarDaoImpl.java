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
        String insertRequest = "INSERT INTO cars (model, manufacturers_id) VALUE (?, ?);";
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
            throw new DataProcessingException("Can't create Car", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT cars.id AS car_id, model, "
                + "m.id as manufacturers_id, m.name, m.country "
                + "FROM cars " + "JOIN manufacturers m " + "ON cars.manufacturers_id = m.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement =
                         connection.prepareStatement(selectRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car by id: " + id, e);
        }

        if (car != null) {
            car.setDrivers(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT cars.id AS car_id, model, "
                + "m.id as manufacturers_id, m.name, m.country "
                + "FROM cars " + "JOIN manufacturers m "
                + "ON cars.manufacturers_id = m.id "
                + "where cars.is_deleted = FALSE ;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement =
                         connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Cars from DB");
        }
        for (Car car : cars) {
            car.setDrivers(getDriverForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, "
                + "manufacturers_id = ? WHERE id = ? "
                + "AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement =
                         connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }
        deleteRelations(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement =
                         connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete Car by index: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverRequest = "SELECT c.id AS car_id, model, manufacturers_id, "
                + "m.name, m.country FROM cars c"
                + " JOIN manufacturers m ON c.manufacturers_id = m.id"
                + " JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsByDriverStatement
                         = connection.prepareStatement(getAllCarsByDriverRequest)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Cars from DB by driver_id: "
                    + driverId, e);
        }
    }

    private void deleteRelations(Car car) {
        String deleteRelationRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteRelationsFromCarStatement =
                         connection.prepareStatement(deleteRelationRequest)) {
            deleteRelationsFromCarStatement.setLong(1, car.getId());
            deleteRelationsFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations from car: " + car, e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addDriversStatement =
                         connection.prepareStatement(insertDriversQuery)) {
            addDriversStatement.setLong(1, car.getId());
            if (car.getDrivers() != null) {
                for (Driver driver : car.getDrivers()) {
                    addDriversStatement.setLong(2, driver.getId());
                    addDriversStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers: " + car, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(
                "manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject(
                "car_id", Long.class));
        return car;
    }

    private List<Driver> getDriverForCar(Long carId) {
        String getAllDriversFromCarRequest = "SELECT id, name, license_number "
                + "FROM drivers " + "JOIN cars_drivers cd "
                + "ON drivers.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement =
                         connection.prepareStatement(getAllDriversFromCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id "
                    + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
}
