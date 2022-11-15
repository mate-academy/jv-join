package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create new car: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, m.id, m.name, m.country "
                + "FROM taxi_service.cars c "
                + "INNER JOIN taxi_service.manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.model, m.id, m.name, m.country "
                + "FROM taxi_service.cars c "
                + "INNER JOIN taxi_service.manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Car> carList = new ArrayList<>();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update the car: " + car, e);
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE taxi_service.cars SET taxi_service.cars.is_deleted = TRUE "
                + "WHERE taxi_service.cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car with id: " + id, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(1, Long.class);
        String model = resultSet.getString(2);
        Long modelId = resultSet.getObject(3, Long.class);
        String name = resultSet.getString(4);
        String country = resultSet.getString(5);
        Manufacturer manufacturer = new Manufacturer(modelId, name, country);
        return new Car(id, model, manufacturer, null);
    }
}
