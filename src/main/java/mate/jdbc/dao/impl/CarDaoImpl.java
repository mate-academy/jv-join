package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {

    @Override
    public Car create(Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(Query.INSERT.getQuery(), Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            car.setId(generatedKeys.getObject(1, Long.class));
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't save car to DB! " + car);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(Query.SELECT_JOIN_MANUFACTURERS.getQuery() + " AND ID = ? ")) {
            statement.setLong(1, id);
            statement.executeUpdate();
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = new Car();
                car.setModel(resultSet.getString(2));
                car.setId(id);
                Long manufacturerId = resultSet.getObject(5, Long.class);
                String manufacturerName = resultSet.getString(6);
                String manufacturerCountry = resultSet.getString(7);
                Manufacturer manufacturer = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
                car.setManufacturer(manufacturer);
                //TODO list<driver>

            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car with id " + id);
        }
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

    private List<Driver> wait(Long carId) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM cars_drivers WHERE car_id = ?")) {
            statement.setLong(1, carId);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            car.setId(generatedKeys.getObject(1, Long.class));
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't save car to DB! " + car);
        }
    }

    private enum Query {
        INSERT("INSERT INTO cars (model, manufacturer_id) VALUE (?, ?)"),
        SELECT_JOIN_MANUFACTURERS("SELECT * FROM cars JOIN manufacturers ON manufacturer_id = manufacturers.id WHERE cars.is_deleted = FALSE"),
        ;

        private final String query;

        Query(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }

}
