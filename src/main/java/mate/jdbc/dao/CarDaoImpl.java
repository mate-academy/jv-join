package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String createCarRequest = "INSERT INTO cars (model, manufacturers_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(createCarRequest,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to create car " + car, e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT c.id, c.model, c.manufacturers_id, mn.name, mn.country "
                                       + "FROM cars c JOIN manufacturers mn "
                                       + "ON c.manufacturers_id = mn.id "
                                       + "WHERE c.id = ? AND c.is_deleted = false";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = prepareCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to get car with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT c.id, c.model, c.manufacturers_id, mn.name, mn.country "
                                       + "FROM cars c "
                                       + "INNER JOIN manufacturers mn ON manufacturers_id = mn.id "
                                       + "WHERE c.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement =
                        connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(prepareCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to get all cars from DB", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars "
                               + "SET model = ?, manufacturers_id = ? "
                               + "WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to update car: " + car, e);
        }
        removeDriverFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement =
                        connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get all cars from DB", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllDriverRequest = "SELECT c.id, model, manufacturers_id, name, country "
                                             + "FROM cars c "
                                             + "JOIN manufacturers mn "
                                             + "ON c.manufacturers_id = mn.id "
                                             + "JOIN cars_drivers cd "
                                             + "ON c.id = cd.car_id "
                                             + "WHERE c.is_deleted = false "
                                             + "AND mn.is_deleted = false "
                                             + "AND driver_id = ?";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriverStatement =
                        connection.prepareStatement(getAllDriverRequest)) {
            getAllDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(prepareCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to get all cars by driver id " + driverId, e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    private Car prepareCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriverForCarRequest = "SELECT id, name, license_number "
                                                + "FROM drivers "
                                                + "JOIN cars_drivers "
                                                + "ON cars_drivers.driver_id = drivers.id "
                                                + "WHERE drivers.is_deleted = false "
                                                + "AND car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getDriverForCarRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(prepareDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to get drivers from DB for car "
                                                      + id, e);
        }
    }

    private Driver prepareDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void addDriversToCar(Car car) {
        List<Driver> drivers = car.getDrivers();
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : drivers) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to add drivers to car: " + car, e);
        }
    }

    private void removeDriverFromCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Unable to delete driver drom car: " + car, e);
        }
    }
}
