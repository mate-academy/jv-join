package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String query = "insert into cars (model, manufacturer_id) values (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not create a car! ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "select c.id as car_id, c.model, m.id as manufacturer_id, m.name, "
                + "m.country from cars c join manufacturers m on c.manufacturer_id = m.id "
                + "where c.id = ? and c.is_deleted = false and m.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not get a car! ", e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "select * from cars where is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getLong("id")));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not get all cars! ", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "update cars set model = ?, manufacturer_id = ? "
                + "where id = ? and is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can not update this car! ", e);
        }
        removeDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "update cars set is_deleted = true where id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not delete a car! ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "select id, model, manufacturer_id from cars c "
                + "join cars_drivers cd on c.id = cd.car_id where cd.driver_id = ? "
                + "and c.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getLong("id")));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not get all cars by driver! ", e);
        }
    }

    private void insertDrivers(Car car) {
        String query = "insert into cars_drivers (car_id, driver_id) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not insert drivers! ", e);
        }
    }

    private void removeDrivers(Car car) {
        String query = "delete from cars_drivers where car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can not remove car's drivers! ", e);
        }
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));

        Car car = new Car();
        car.setId(resultSet.getLong("car_id"));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);

        return car;
    }

    private List<Driver> getCarDrivers(Long id) {
        String query = "select id, name, license_number from drivers d "
                + "join cars_drivers cd on d.id = cd.driver_id where cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not get car's drivers! ", e);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getLong("id"));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

}
