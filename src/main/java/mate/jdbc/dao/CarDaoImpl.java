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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
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
        String query = "SELECT * FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id WHERE c.id = ?"
                + " AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get the car by id" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String query = "SELECT * FROM cars c JOIN manufacturers m ON "
                + "c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
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
        for (Car carFromList : carList) {
            carFromList.setDrivers(getDriversByCar(carFromList.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars c SET model = ?, manufacturer_id = ? "
                + "WHERE c.id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(query)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car " + car, e);
        }
        deleteDriversFromCar(car.getId());
        insertDrivers(car);
        return car;
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
        String query = "SELECT c.id, model, manufacturer_id, m.id, m.name, m.country "
                + "FROM cars c "
                + "LEFT JOIN cars_drivers c_d ON car_id = c.id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c_d.driver_id = ?";
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
        for (Car carFromList : carList) {
            carFromList.setDrivers(getDriversByCar(carFromList.getId()));
        }
        return carList;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));
        car.setManufacturer(new Manufacturer(resultSet.getObject(4, Long.class),
                resultSet.getString(5), resultSet.getString(6)));
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject(1, Long.class),
                resultSet.getString(2),
                resultSet.getString(3));
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection
                        .prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert driver into "
                    + " cars_drivers by car " + car, e);
        }
    }

    private void deleteDriversFromCar(Long carsId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, carsId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete drivers"
                    + " from cars_drivers by id " + carsId, e);
        }
    }

    private List<Driver> getDriversByCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT d.id, name,licenceNumber FROM drivers d "
                + "JOIN cars_drivers c_d ON c_d.driver_id = d.id "
                + "WHERE c_d.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers "
                    + "from cars_drivers by id " + carId, e);
        }
        return drivers;
    }
}
