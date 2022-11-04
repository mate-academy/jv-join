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
        String query = "INSERT INTO car (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        addCarDrivers(car.getDrivers(), car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, c.model as car_model, "
                + "m.id as manufacturer_id, m.name as manufacturer_name, "
                + "m.country as manufacturer_country "
                + "FROM car c "
                + "JOIN manufacturer m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE AND m.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, c.model as car_model, "
                + "m.id as manufacturer_id, m.name as manufacturer_name, "
                + "m.country as manufacturer_country "
                + "FROM car c "
                + "JOIN manufacturer m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = false";
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            cars = getCarList(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars from car table", e);
        }
        cars.forEach(car -> car.setDrivers(getCarDrivers(car)));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE car "
                + "SET "
                + "manufacturer_id = ?, "
                + "model = ? "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in db.", e);
        }
        removeDriversFromCar(car);
        addCarDrivers(car.getDrivers(), car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE car SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id as car_id, c.model as car_model, "
                + "m.id as manufacturer_id, m.name as manufacturer_name, "
                + "m.country as manufacturer_country "
                + "FROM car c "
                + "JOIN manufacturer m ON c.manufacturer_id = m.id "
                + "JOIN car_driver cd ON c.id = cd.car_id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = false "
                + "AND cd.driver_id = ?";
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            cars = getCarList(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars from car table", e);
        }
        cars.forEach(car -> car.setDrivers(getCarDrivers(car)));
        return cars;
    }

    private void addCarDrivers(List<Driver> drivers, Car car) {
        String insertCarDriverSql = "INSERT INTO car_driver (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertCarDriverSql)
                ) {
            for (Driver driver : drivers) {
                statement.setLong(1, car.getId());
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("car_model");
        Long manufacturerId
                = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String manufacturerCountry = resultSet.getString("manufacturer_country");
        Manufacturer manufacturer
                = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        return new Car(id, model, manufacturer, null);
    }

    private List<Car> getCarList(ResultSet resultSet) throws SQLException {
        List<Car> cars = new ArrayList<>();
        while (resultSet.next()) {
            cars.add(getCar(resultSet));
        }
        return cars;
    }

    private List<Driver> getCarDrivers(Car car) {
        String query = "SELECT d.id as id, d.name as name, d.license_number as license_number "
                + "FROM car c "
                + "JOIN car_driver cd ON c.id = cd.car_id "
                + "JOIN driver d ON cd.driver_id = d.id "
                + "WHERE c.id =  ? AND c.is_deleted = false AND d.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                // TODO: 03.11.2022 Replace with mapper calling
                Long id = resultSet.getObject("id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                drivers.add(new Driver(id, name, licenseNumber));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeDriversFromCar(Car car) {
        String query = "DELETE FROM car_driver "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
