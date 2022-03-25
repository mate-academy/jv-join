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
        String query = "INSERT INTO cars (model,manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(query,
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

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (driver_id, car_id) value (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriversStatement =
                         connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(1, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to DB.", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id as car_id, c.model as car_model, m.id as"
                + " manufacturer_id, m.name as manufacturer_name, m.country as "
                + "manufacturer_country FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id  WHERE c.id = ? and c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement =
                         connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
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

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars c SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? and is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a manufacturer "
                    + car, e);
        }
        deleteCarDriverRelations(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete the car with given id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, c.model as car_model, m.id as"
                + " manufacturer_id, m.name as manufacturer_name, m.country as "
                + "manufacturer_country FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id where c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from cars DB", e);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> allDriverCars = new ArrayList<>();
        String getAllDriverCarsRequest = "select cd.car_id,"
                + "m.id as manufacturer_id, "
                + "m.name as manufacturer_name, "
                + "m.country as manufacturer_country, "
                + "c.model as car_model "
                + "from cars_drivers cd "
                + "join cars c on cd.car_id = c.id "
                + "join manufacturers m on m.id = c.manufacturer_id "
                + "where cd.driver_id = ? and c.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriverCarsStatement =
                         connection.prepareStatement(getAllDriverCarsRequest)) {
            getAllDriverCarsStatement.setLong(1,driverId);
            ResultSet resultSet = getAllDriverCarsStatement.executeQuery();
            while (resultSet.next()) {
                allDriverCars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id = " + driverId, e);
        }
        allDriverCars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return allDriverCars;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCar = "SELECT id, name, licenseNumber FROM drivers d JOIN"
                + " cars_drivers cd ON d.id = cd.driver_id where cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement =
                         connection.prepareStatement(getAllDriversForCar)) {
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

    private boolean deleteCarDriverRelations(Long carId) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(deleteRequest)) {
            statement.setLong(1, carId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete relations "
                    + "of car with id " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }
}
