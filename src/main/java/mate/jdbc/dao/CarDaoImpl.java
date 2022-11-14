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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                            Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturerId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not creat new car. Params: id="
                    + car.getId()
                    + ", model=" + car.getModel()
                    + ", manufacturer_id=" + car.getManufacturerId(), e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT cars.id, cars.model, "
                + "manufacturers.id as manufacturer_id, manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = false;";
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = createNewCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can not get car by id. id=" + id,e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, "
                + "manufacturers.id as manufacturer_id, manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(createNewCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not get info from cars, manufacturers dbs", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT drivers.name, drivers.license_number,"
                + " manufacturers.name as manufacturer, manufacturers.country, "
                + "cars.id, cars.model "
                + "FROM  cars_drivers "
                + "INNER JOIN cars on cars_drivers.car_id = cars.id "
                + "inner JOIN manufacturers on cars.manufacturer_id = manufacturers.id "
                + "INNER JOIN drivers on cars_drivers.driver_id = drivers.id "
                + "WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(createNewCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("can not get a list of cars of driver. "
                    + "Params: id=" + driverId, e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE cars.id = ? AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturerId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can not update car. Params: "
                    + "id=" + car.getId()
                    + ", model=" + car.getModel()
                    + ", manufacturer_id=" + car.getManufacturerId(), e);
        }
        addDriverToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true "
                + "WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            statement.executeUpdate();
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can not delete car by id. Params: "
                    + "id=" + id, e);
        }
    }

    private Car createNewCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setManufacturerId(resultSet.getLong("manufacturer_id"));
        car.setId(resultSet.getLong("id"));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void addDriverToCar(Car car) {
        if (car.getDrivers().isEmpty()) {
            return;
        }
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("can not add driver to car. Params:"
                   + car, e);
        }
    }
}
