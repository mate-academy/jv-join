package mate.jdbc.dao;

import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, color, price, driver_id) "
                + "VALUES (?, ?, ?, ?) ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement saveCarStatement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setString(2, car.getColor());
            saveCarStatement.setBigDecimal(3, car.getPrice());
            saveCarStatement.setLong(4, car.getDriver().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, model, color, price,\n" +
                "\t\td.id AS driver_id, d.license_number, d.name\n" +
                "\tFROM cars c \n" +
                "    JOIN\n" +
                "\t\tdrivers d ON c.driver_id = d.id\n" +
                "    WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
                Long newId = resultSet.getObject("driver_id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                Driver driver = new Driver(name, licenseNumber);
                driver.setId(newId);
                car.setDriver(driver);
            }
            return Optional.ofNullable(car);
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, color, price,\n" +
                "\t\td.id AS driver_id, d.license_number, d.name\n" +
                "\tFROM cars c \n" +
                "    JOIN\n" +
                "\t\tdrivers d ON c.driver_id = d.id\n" +
                "    WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllCarssStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarssStatement.executeQuery();
            while (resultSet.next()) {
                Car car  = getCar(resultSet);
                Long newId = resultSet.getObject("driver_id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                Driver driver = new Driver(name, licenseNumber);
                driver.setId(newId);
                car.setDriver(driver);
                cars.add(car);
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, color = ?,"
                + "price = ?, driver_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarStatement
                     = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setString(2, car.getColor());
            updateCarStatement.setBigDecimal(3, car.getPrice());
            updateCarStatement.setLong(4, car.getDriver().getId());
            updateCarStatement.setLong(5, car.getId());
            updateCarStatement.executeUpdate();
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarStatement
                     = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a car by id " + id + " ",
                    throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        String color = resultSet.getString("color");
        Long driver_id = resultSet.getObject("driver_id", Long.class);
        Car car = new Car(model, color);
        car.setId(newId);
        return car;
    }

}
