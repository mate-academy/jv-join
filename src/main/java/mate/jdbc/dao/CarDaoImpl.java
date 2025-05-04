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
import mate.jdbc.lib.Inject;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private ManufacturerService manufacturerService;

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars(model, manufacturer_id) values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
            if (car.getDrivers().size() > 0) {
                fillRelations(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car with model "
                    + car.getModel(), e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS cars_id, model, "
                + "manufacturers.id AS manufacturers_id, name, country"
                + "FROM cars"
                + "INNER JOIN manufacturers"
                + "ON cars.manufacturer_id = manufacturers.id"
                + "WHERE cars.is_deleted = 0 AND cars.id = 1;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Car output;
            if (resultSet.next()) {
                output = parseCar(resultSet);
                output.setId(id);
                output.setDrivers(getDriversForCar(id));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id = " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        List<Car> output = new ArrayList<>();
        String query = "SELECT cars.id AS id, model, "
                + "manufacturers.id AS manufacturers_id, name, country "
                + "FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = 0;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car current = parseCar(resultSet);
                current.setId(resultSet.getObject("id", Long.class));
                current.setDrivers(getDriversForCar(current.getId()));
                output.add(current);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all data from cars table", e);
        }
        return output;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            deleteRelations(car);
            fillRelations(car);
            if (statement.executeUpdate() >= 1) {
                return car;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with model"
                    + car.getModel() + " and id "
                    + car.getId(), e);
        }
        throw new DataProcessingException("Can't update driver with name"
                + car.getModel() + " and id "
                + car.getId() + ". DB rows updated less than 1");
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() >= 1) {
                return true;
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id" + id, e);
        }
        throw new DataProcessingException("Can't delete car with id: "
                + id + ". DB rows updated less than 1");
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> output = new ArrayList<>();
        String query = "SELECT manufacturer_id, model, cars.id "
                + "FROM cars "
                + "INNER JOIN cars_drivers "
                + "ON cars.id = car_id "
                + "WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getObject("id", Long.class);
                String model = resultSet.getString("model");
                Manufacturer manufacturer = manufacturerService.get(
                        resultSet.getObject("manufacturer_id", Long.class));
                Car current = new Car(id, model, manufacturer);
                current.setDrivers(getDriversForCar(id));
                output.add(current);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id = " + driverId, e);
        }
        return output;
    }

    private void fillRelations(Car car) {
        String query = "INSERT INTO cars_drivers(driver_id, car_id) values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert relation between car with id "
                    + car.getId() + " and drivers", e);
        }
    }

    private void deleteRelations(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert relation between car with id "
                    + car.getId() + " and drivers", e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getString("name"), resultSet.getString("country"));
        return new Car(id, model, manufacturer);
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> output = new ArrayList<>();
        String query = "SELECT id, name, license_number "
                + "FROM cars_drivers "
                + "INNER JOIN drivers "
                + "ON driver_id = id "
                + "WHERE car_id = ? AND is_deleted = 0;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getObject("id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                Driver driver = new Driver(name, licenseNumber);
                driver.setId(id);
                output.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers for car with id "
                    + carId, e);
        }
        return output;
    }
}
