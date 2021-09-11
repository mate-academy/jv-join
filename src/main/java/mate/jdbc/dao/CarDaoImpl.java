package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createCarRequest = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createCarRequest,
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
            throw new RuntimeException("Can't insert car " + car + " to DB.", e);
        }
        if (car.getDrivers() != null) {
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(2, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers to car " + car, e);
        }
    }

    @Override
    public Car get(Long id) {
        String getCarRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id,\n"
                + "m.country AS country, m.name AS name\n"
                + "FROM cars c JOIN manufacturers m\n"
                + "ON c.manufacturer_id = m.id\n"
                + "WHERE c.id = ? AND c.is_deleted = false;";
        Car car = null;

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car with id #" + id + " from DB.", e);
        }

        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id,\n"
                + "m.country AS country, m.name AS name\n"
                + "FROM cars c JOIN manufacturers m\n"
                + "ON c.manufacturer_id = m.id\n"
                + "WHERE c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery(getAllCarsRequest);
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars from DB.", e);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?,\n"
                                    + "manufacturer_id = ?\n"
                                    + "WHERE id = ? AND is_deleted = false;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car " + car + " in DB.", e);
        }
        if (car.getDrivers() != null) {
            removeDrivers(car);
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = true WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, carId);
            int numberOfDeletedRows = deleteCarStatement.executeUpdate();
            return numberOfDeletedRows >= 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id" + carId + " from DB.", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT * FROM cars c JOIN cars_drivers cd\n"
                                        + "ON c.id = cd.car_id\n"
                                        + "JOIN drivers d ON cd.driver_id = d.id\n"
                                        + "JOIN manufacturers m ON c.manufacturer_id = m.id\n"
                                        + "WHERE cd.driver_id = ?;";
        List<Car> allCars = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                allCars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars by driver " + driverId + " from DB.", e);
        }
        return allCars;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
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

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest =
                "SELECT id, driver_name, license_number FROM drivers d\n"
                + "JOIN cars_drivers cd\n"
                + "ON d.id = cd.driver_id\n"
                + "WHERE cd.car_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversForCarStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers for car with id #"
                    + carId + " in DB.", e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void removeDrivers(Car car) {
        String removeDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriverStatement =
                        connection.prepareStatement(removeDriversRequest)) {
            removeDriverStatement.setLong(1, car.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Can't remove drivers from car " + car, e);
        }
    }
}
