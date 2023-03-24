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
        String insertCarRequest = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement insertCarStatement = connection.prepareStatement(insertCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setObject(2, car.getManufacturer());
            ResultSet resultSet = insertCarStatement.executeQuery();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't insert car " + car + " into DB", e);
        }
        car.setDrivers(getDriversForCar(car.getId()));
        return car;
    }

    @Override
    public Car get(Long id) {
        Car car = null;
        String getCarByIdRequest = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country \n" +
                "FROM cars c JOIN manufacturers m\n" +
                "ON c.manufacturer_id = m.id\n" +
                "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(getCarByIdRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSetWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car from DB with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    private Car parseCarFromResultSetWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversForCarRequest =
                "SELECT id, name, licenseNumber FROM taxi_db.drivers" +
                        " JOIN taxi_db.cars_drivers ON drivers.id = cars_drivers.driver_id" +
                        " WHERE cars_drivers.car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversStatement = connection.prepareStatement(getDriversForCarRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> driversList = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver(resultSet.getObject("id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("licenseNumber"));
                driversList.add(driver);
            }
            return driversList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> carsList = new ArrayList<>();
        String getAllCarsRequest = "SELECT *, manufacturers.name, manufacturers.country FROM cars\n" +
                "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id\n" +
                "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarFromResultSetWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't get List of cars from database", e);
        }
        carsList.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return carsList;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarStatement = connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't update the car in the database " + car, e);
        }
        return car;
    }
    //1. Update car fields
    //2. Delete all relations in cars_drivers table where carId = car.getId()
    //3. Add new relations to the cars_drivers table

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't delete car from database with id " + id, e);
        }
    }
}
