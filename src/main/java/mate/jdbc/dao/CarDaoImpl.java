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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarRequest = "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement createCarStatement = connection
                            .prepareStatement(createCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't create car " + car, e);
        }
        insertRelationsForCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectCarByIdRequest = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getCarByIdStatement = connection
                            .prepareStatement(selectCarByIdRequest)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllCarsStatement = connection
                            .prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of cars from DB. ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriverForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement updateStatement = connection
                            .prepareStatement(updateCarRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car " + car, e);
        }
        deleteAllRelationsForCar(car);
        insertRelationsForCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarByIdRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement softDeleteStatement = connection
                            .prepareStatement(deleteCarByIdRequest)) {
            softDeleteStatement.setLong(1, id);
            return softDeleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllCarsByDriverIdRequest = "SELECT c.id as car_id, model, c.manufacturer_id,"
                + " m.name, m.country FROM cars c"
                + " JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " JOIN cars_drivers cd ON c.id = cd.car_id "
                + " WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllByDriverStatement = connection
                            .prepareStatement(getAllCarsByDriverIdRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers in DB by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriverForCar(car.getId()));
        }
        return cars;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
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

    private List<Driver> getDriverForCar(Long carId) {
        String getDriverForCarRequest = "SELECT d.id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllDriversStatement = connection
                            .prepareStatement(getDriverForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers in DB by car id " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void deleteAllRelationsForCar(Car car) {
        String deleteAllRelationsFroCarRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement deleteAllRelationsStatement = connection
                            .prepareStatement(deleteAllRelationsFroCarRequest)) {
            deleteAllRelationsStatement.setLong(1, car.getId());
            deleteAllRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete relation for car " + car, e);
        }
    }

    private void insertRelationsForCar(Car car) {
        String insertRelationsForCarRequest = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement insertRelationsStatement = connection
                            .prepareStatement(insertRelationsForCarRequest)) {
            insertRelationsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertRelationsStatement.setLong(2, driver.getId());
                insertRelationsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert new relations for car " + car, e);
        }
    }
}
