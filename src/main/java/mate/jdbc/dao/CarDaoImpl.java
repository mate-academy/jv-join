package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUE(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setObject(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet generatedKeys = createStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create, in db, new car " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars JOIN manufacturers ON "
                + "cars.manufacturer_id = manufacturers.id WHERE cars.id = ?"
                + " AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get the car by id" + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String query = "SELECT * FROM cars JOIN manufacturers ON "
                + "cars.manufacturer_id = manufacturers.id WHERE cars.is_deleted = FALSE;";
        Car car;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                carList.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all data from db", e);
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE cars.id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
            if (updateStatement.executeUpdate() > 0) {
                return car;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car " + car, e);
        }
        deleteDriversFromCars_Drivers(car.getId());
        insertDrivers(car);
        throw new NoSuchElementException("car does`t exist in DB");
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
            return (statement.executeUpdate() > 0);
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete data by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carList = new ArrayList<>();
        String query = "SELECT cars.id, model, manufacturer_id,manufacturers.id,"
                + " manufacturers.name,manufacturers.country  "
                + "FROM cars"
                + " LEFT JOIN cars_drivers ON cars_id = cars.id "
                + " LEFT JOIN manufacturers ON cars.manufacturer_id = manufacturers.id"
                + " WHERE drivers_id = ?";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getIdCarStatement = connection.prepareStatement(query)) {
            getIdCarStatement.setLong(1, driverId);
            ResultSet resultSet = getIdCarStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all by driver " + driverId, e);
        }
        // get info about drivers for each car
        return carList;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));
        car.setManufacturer(new Manufacturer(resultSet.getObject(4, Long.class),
                resultSet.getString(5), resultSet.getString(6)));
        car.setDriver(getDriversFromCars_drivers(resultSet.getLong(1)));
        return car;
    }

    private List<Driver> getDriversFromCars_drivers(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT drivers.id, name,licenceNumber FROM drivers "
                + "JOIN cars_drivers ON cars_drivers.drivers_id = drivers.id "
                + "WHERE cars_drivers.drivers_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers from cars_drivers by id " + id, e);
        }
        return drivers;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject(1, Long.class),
                resultSet.getString(2),
                resultSet.getString(3));
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (cars_id, drivers_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection
                        .prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriver()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert driver into "
                    + " cars_drivers by car " + car, e);
        }
    }

    private void deleteDriversFromCars_Drivers(Long id) {
        String query = "DELETE FROM cars_drivers WHERE cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete drivers"
                    + " from cars_drivers by id " + id, e);
        }
    }

}
