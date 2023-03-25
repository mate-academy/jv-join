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
        String createCarRequest =
                "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        for (Driver driver : car.getDrivers()) {
            addDriver(car.getId(), driver.getId());
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT cars.id AS id, model,"
                + "name AS manufacturer_name, "
                + "manufacturers.id AS manufacturer_id, "
                + "country AS manufacturer_country "
                + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND NOT is_deleted;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT cars.id AS id, model,"
                + "name AS manufacturer_name, "
                + "manufacturers.id AS manufacturer_id, "
                + "country AS manufacturer_country "
                + "FROM cars JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE NOT cars.is_deleted;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsRequest = "SELECT cars.id AS id, model, "
                + "name AS manufacturer_name, "
                + "manufacturers.id AS manufacturer_id, "
                + "country AS manufacturer_country "
                + "FROM cars JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "JOIN cars_drivers ON cars.id = car_id "
                + "WHERE NOT cars.is_deleted AND cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement =
                        connection.prepareStatement(getAllCarsRequest)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driver from DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(updateRequest)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car "
                    + car + ".", e);
        }
        clearDrivers(car.getId());
        for (Driver driver : car.getDrivers()) {
            addDriver(car.getId(), driver.getId());
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        clearDrivers(id);
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        String manufacturerName = resultSet.getString("manufacturer_name");
        String country = resultSet.getString("manufacturer_country");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        return new Car(carId, model,
                new Manufacturer(manufacturerId, manufacturerName, country));
    }

    private List<Driver> getDrivers(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT id, name, license_number "
                + "FROM drivers JOIN cars_drivers ON driver_id = id "
                + "WHERE is_deleted = FALSE AND car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long driverId = resultSet.getObject("id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                drivers.add(new Driver(id, name, licenseNumber));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers to car with id "
                    + id + ".", e);
        }
        return drivers;
    }

    private void clearDrivers(Long id) {
        String deleteDriversRequest =
                "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(deleteDriversRequest)) {
            getAllCarsStatement.setLong(1, id);
            getAllCarsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers from DB.", e);
        }
    }

    private void addDriver(Long carId, Long driverId) {
        String addDriverRequest =
                "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(addDriverRequest)) {
            getAllCarsStatement.setLong(1, carId);
            getAllCarsStatement.setLong(2, driverId);
            getAllCarsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to DB.", e);
        }
    }
}
