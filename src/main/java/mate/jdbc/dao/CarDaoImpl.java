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
        String query = "INSERT INTO cars(model, manufacturer_id) "
                + "VALUES(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't create car. " + car, throwables);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, model, "
                + "cars.manufacturer_id, manufacturers.name, manufacturers.country\n"
                + "FROM cars\n"
                + "JOIN manufacturers\n"
                + "ON manufacturers.id = cars.manufacturer_id\n"
                + "WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                            = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id  " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(id));
        }
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() throws SQLException {
        Car newCar = null;
        List<Car> cars = new ArrayList<>();
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT cars.id, "
                + "cars.model, cars.manufacturer_id, "
                + " manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON manufacturers.id = manufacturer_id "
                + "ORDER BY cars.id;";
        ResultSet resultSet = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                newCar = getCar(resultSet);
                cars.add(newCar);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't udate car by id " + car.getId(), e);
        }
        removeDriversRelations(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT  car_id, cars.model,"
                + " manufacturer_id, manufacturers.name, country "
                + "FROM cars_drivers "
                + "JOIN drivers "
                + "ON driver_id = drivers.id "
                + "JOIN cars "
                + "ON cars.id = car_id "
                + "JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driverId " + driverId, e);
        }
        return cars;
    }

    public void addDriverToCar(Driver driver, Car car) {
        String query = "INSERT INTO cars_drivers VALUES(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.setLong(2, driver.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car ", e);
        }
    }

    public void removeDriversRelations(Car car) {
        String query = "DELETE from cars_drivers "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't remove drivers from car by car id  " + car.getId(), e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer;
        Long carId = resultSet.getObject("cars.id", Long.class);
        String model = resultSet.getString("cars.model");
        Long manufacturerId = resultSet.getObject("cars.manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturers.name");
        String manufacturerCountry = resultSet.getString("manufacturers.country");
        manufacturer = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        return new Car(carId, model, manufacturer);
    }

    private List<Driver> getDrivers(Long carId) {
        String query = "SELECT  "
                + "drivers.id, drivers.name, drivers.license_number "
                + "FROM  drivers"
                + " JOIN  cars_drivers "
                + "ON driver_id = drivers.id "
                + "WHERE car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by id  " + carId, e);
        }
        return drivers;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("drivers.id", Long.class));
        driver.setName(resultSet.getString("drivers.name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(car_id,driver_id) "
                + "VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't insert drivers by car  id" + car.getId(), e);
        }
    }

    private Driver getDriver(Long carId) {
        Driver driver = new Driver();
        String query = "SELECT drivers.id, drivers.name, "
                + "license_number  FROM taxy_service.drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = driver_id "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                driver = parseDriverFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car  id" + carId, e);
        }
        return driver;
    }
}
