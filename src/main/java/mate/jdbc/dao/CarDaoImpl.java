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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car to DB. Car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id as car_id, c.model as car_model, m.id as"
                + " manufacturer_id, m.name as manufacturer_name, m.country as "
                + "manufacturer_country FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id  where c.id = ? and c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectAllRequest = "SELECT c.id as car_id, c.model as car_model, m.id as"
                + " manufacturer_id, m.name as manufacturer_name, m.country as "
                + "manufacturer_country FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id where c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(selectAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all data from cars table", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars c SET model = ?, manufacturer_id = ?"
                + " where id = ? and is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car in DB. Car: " + car, e);
        }
        deleteAllRelationsBetweenCarAndDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = true where id = ?;";
        Optional<Car> carOptional = get(id);
        Car car = carOptional.orElseThrow(() -> new DataProcessingException("Couldn't delete"
                + " relations between drivers and car. ID car: " + id));
        deleteAllRelationsBetweenCarAndDrivers(car);
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriverRequest = "SELECT c.id as car_id, c.model as car_model, "
                + "m.id as manufacturer_id, m.name as manufacturer_name, m.country as "
                + "manufacturer_country FROM cars c JOIN cars_drivers cd ON c.id = "
                + "cd.car_id JOIN manufacturers m ON c.manufacturer_id = m.id where"
                + " cd.driver_id = ? and c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement =
                        connection.prepareStatement(getCarsByDriverRequest)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't find cars by driver's id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car. Car: " + car, e);
        }
    }

    private void deleteAllRelationsBetweenCarAndDrivers(Car car) {
        String deleteAllRelationsRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteAllRelationsStatement =
                        connection.prepareStatement(deleteAllRelationsRequest)) {
            deleteAllRelationsStatement.setLong(1, car.getId());
            deleteAllRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from car. Car: " + car, e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_model"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM drivers d JOIN"
                + " cars_drivers cd ON d.id = cd.driver_id where cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find any drivers by car_id: " + carId, e);
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
