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
import java.util.ArrayList;
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
            throw new DataProcessingException("Can't save car to DB! " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(Query.SELECT_JOIN_MANUFACTURERS.getQuery() + " AND cars.id = ? ")) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car car = null;
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(Query.SELECT_JOIN_MANUFACTURERS.getQuery())) {
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
    }

    @Override
    public Car update(Car car) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(Query.UPDATE.getQuery())) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car!" + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE cars SET is_deleted = TRUE WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id " + id, e);
        }
    }

    private List<Driver> getDriversByCarId(Long carId) {
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(Query.SELECT_DRIVERS.getQuery())) {
            statement.setLong(1, carId);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                String name = resultSet.getString(2);
                String licenseNumber = resultSet.getString(3);
                drivers.add(new Driver(id, name, licenseNumber));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers with this car id " + carId);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString(2));
        car.setId(resultSet.getObject(1, Long.class));
        Long manufacturerId = resultSet.getObject(5, Long.class);
        String manufacturerName = resultSet.getString(6);
        String manufacturerCountry = resultSet.getString(7);
        Manufacturer manufacturer = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        car.setManufacturer(manufacturer);
        car.setDrivers(getDriversByCarId(car.getId()));
        return car;
    }

    private enum Query {
        INSERT("INSERT INTO cars (model, manufacturer_id) VALUE (?, ?)"),
        SELECT_JOIN_MANUFACTURERS("SELECT * FROM cars JOIN manufacturers ON manufacturer_id = manufacturers.id WHERE cars.is_deleted = FALSE"),
        SELECT_DRIVERS("SELECT * FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.is_deleted = false"),
        UPDATE("UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ? AND is_deleted = FALSE"),
        DELETE("");


        private final String query;

        Query(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }

}
