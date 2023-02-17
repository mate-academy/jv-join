package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
        String createCarRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        String createCarDriverRequest =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS);
                PreparedStatement carDriverStatement =
                        connection.prepareStatement(createCarDriverRequest)) {
            carStatement.setString(1, car.getModel());
            carStatement.setLong(2, car.getManufacturer().getId());
            carStatement.executeUpdate();
            ResultSet resultSet = carStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }

            addCarsDriversToDB(carDriverStatement, car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
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
            car.setDrivers(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE";
        List<Car> allCar = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allCar.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        for (Car c : allCar) {
            c.setDrivers(getDriverForCar(c.getId()));
        }
        return allCar;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        String deleteDriverRequest = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        String insertDriverRequest = "INSERT INTO cars_drivers "
                + "(car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement
                        = connection.prepareStatement(query);
                PreparedStatement deleteDriverStatement
                        = connection.prepareStatement(deleteDriverRequest);
                PreparedStatement insertDriverStatement
                        = connection.prepareStatement(insertDriverRequest)) {
            carStatement.setString(1, car.getModel());
            carStatement.setLong(2, car.getManufacturer().getId());
            carStatement.setLong(3, car.getId());
            carStatement.executeUpdate();

            deleteDriverStatement.setLong(1, car.getId());
            deleteDriverStatement.executeUpdate();

            addCarsDriversToDB(insertDriverStatement, car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return getAll().stream()
                .filter(c -> c.getDrivers().stream().anyMatch(d -> d.getId().equals(driverId)))
                .collect(Collectors.toList());
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getLong("cars.id"));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getLong("manufacturer_id"));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriverForCar(Long carId) {
        String driversRequest = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE car_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriverStatement =
                        connection.prepareStatement(driversRequest)) {
            getAllDriverStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriverStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't find drivers in DB for car id " + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getLong("id"));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void addCarsDriversToDB(PreparedStatement preparedStatement, Car car)
                                                            throws SQLException {
        preparedStatement.setLong(1, car.getId());
        for (Driver d : car.getDrivers()) {
            preparedStatement.setLong(2, d.getId());
            preparedStatement.executeUpdate();
        }
    }
}
