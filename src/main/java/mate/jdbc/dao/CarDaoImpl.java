package mate.jdbc.dao;

import java.sql.*;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create Car. " + car, e);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        Car car = new Car();
        String query = "SELECT cars.id, cars.model, manufacturer_id, manufacturers.name,"
                + " manufacturers.country FROM cars INNER JOIN manufacturers ON "
                + "cars.manufacturer_id = manufacturers.id WHERE cars.is_deleted"
                + " = FALSE AND manufacturers.is_deleted = FALSE AND cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection
                     .prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car.setModel(resultSet.getString("model"));
                car.setId(id);
                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
                manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
                car.setManufacturer(manufacturer);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot find car from DB by id = " + id, e);
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
