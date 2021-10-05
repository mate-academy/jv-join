package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (manufacturer_id, model)"
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createStatement =
                     connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setLong(1, car.getManufacturer().getId());
            createStatement.setString(2, car.getModel());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country " +
                "FROM cars c " +
                "JOIN manufacturers m ON c.manufacturer_id = m.id " +
                "WHERE c.id = ? ";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createStatement =
                     connection.prepareStatement(query)) {
            createStatement.setLong(1, id);
            ResultSet resultSet = createStatement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCarParser(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get car by ID: " + id, throwables);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country " +
                "FROM cars c " +
                "JOIN manufacturers m ON c.manufacturer_id = m.id ";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createStatement =
                     connection.prepareStatement(query)) {
            ResultSet resultSet = createStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarParser(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get all cars from DB");
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }

    private static Car getCarParser(ResultSet resultSet) throws SQLException {
        String model = resultSet.getString("model");
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Long manufacturer_id = resultSet.getObject("manufacturer_id", Long.class);
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturer_id);
        Car car = new Car(manufacturer, model);
        car.setId(resultSet.getObject("c.id", Long.class));
        return car;
    }
}
