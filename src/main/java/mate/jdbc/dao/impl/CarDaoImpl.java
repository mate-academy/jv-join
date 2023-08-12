package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection.prepareStatement(query,
                     Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());

            Manufacturer manufacturer = car.getManufacturer();
            if (manufacturer != null) {
                createCarStatement.setLong(2, manufacturer.getId());
            } else {
                throw new DataProcessingException("Manufacturer cannot be null.");
            }
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        /*if (car.getId() != null && car.getDrivers() != null && car.getDrivers().size() > 0) {
            addDriverCarRelations(car);
        }*/
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * " //id, manufacturer_id, model
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            //Car car = null;
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);//getCar was
                // car = getCarFromResultSet(resultSet);
            }
            /*if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }*/
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * " // * = c.id AS car_id, model, manufacturer_id, name, country
        + "FROM cars c INNER JOIN manufacturers m "
        + "ON c.manufacturer_id = m.id "
        + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
            return cars; //
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from carsDB.", e);
        }

        /*if (!cars.isEmpty()) {
            cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        }*/
    }

    @Override
    public Car update(Car car) {
        //change
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement
                     = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setString(2, car.getManufacturer().toString());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cd.car_id FROM cars_drivers cd "
                + "JOIN drivers d ON d.id = cd.driver_id "
                + "WHERE cd.driver_id = ? AND d.is_deleted = FALSE;";
        List<Long> carIdList = new ArrayList<>();
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carIdList.add(resultSet.getObject("car_id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't get cars IDs from DB by the driver ID: " + driverId, e);
        }
        if (!carIdList.isEmpty()) {
            carIdList.forEach(carId -> get(carId).ifPresent(cars::add));
        }
        return cars;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        return new Car(
                resultSet.getObject("car_id", Long.class),
                resultSet.getString("model"),
                new Manufacturer(
                        resultSet.getObject("manufacturer_id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("country")), null);
    }
}
