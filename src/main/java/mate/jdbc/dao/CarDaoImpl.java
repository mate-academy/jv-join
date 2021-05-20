package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String createQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(createQuery,
                                 Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create new car in DB with "
                    + "parameters: Car-" + car, e);
        }
        if (car != null) {
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT * FROM cars c "
                + "JOIN manufacturers m on c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE "
                + "AND m.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement gerCarStatement = connection.prepareStatement(getCarQuery)) {
            gerCarStatement.setLong(1, id);
            ResultSet resultSet = gerCarStatement.executeQuery();
            if (resultSet.next()) {
                car = createCarWithResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car on id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriverByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarQuery = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE "
                + "AND m.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                Statement getAllStatement = connection.createStatement()) {
            ResultSet resultSet = getAllStatement.executeQuery(getAllCarQuery);
            while (resultSet.next()) {
                Car car = createCarWithResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB", e);
        }
        cars.stream()
                .forEach(n -> n.setDrivers(getDriverByCarId(n.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update cat in DB. Car id" + car.getId(), e);
        }
        deleteDriversRelations(car.getId());
        createNewDriversRelations(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't deleted car. Car id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> allCarDriversById = new ArrayList<>();
        String getAllDriversByIdQuery = "SELECT cd.cars_id, cd.drivers_id, c.manufacturer_id,"
                + " c.model, m.name, m.country "
                + "FROM cars_drivers cd "
                + "JOIN cars c ON c.id = cd.cars_id "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "WHERE cd.drivers_id = ? "
                + "AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement =
                        connection.prepareStatement(getAllDriversByIdQuery)) {
            getAllStatement.setLong(1, driverId);
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                allCarDriversById.add(createCarWithResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers", e);
        }
        allCarDriversById.stream()
                .forEach(c -> c.setDrivers(getDriverByCarId(c.getId())));
        return allCarDriversById;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversQuery,
                                Statement.RETURN_GENERATED_KEYS)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver: car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver", e);
        }
    }

    private Car createCarWithResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        car.setId(carId);
        car.setModel(model);

        Manufacturer manufacturer = new Manufacturer();
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        manufacturer.setId(manufacturerId);
        manufacturer.setName(manufacturerName);
        manufacturer.setCountry(manufacturerCountry);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriverByCarId(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getDriversQuery = "SELECT * FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.id = cd.drivers_id "
                + "WHERE cd.cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getDriversQuery)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(createDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't give driver by id " + carId, e);
        }
    }

    private Driver createDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        driver.setId(id);
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    private void deleteDriversRelations(Long carId) {
        String deleteRelations = "DELETE FROM cars_drivers WHERE cars_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement =
                        connection.prepareStatement(deleteRelations)) {
            deleteRelationsStatement.setLong(1, carId);
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car and "
                    + "drivers relations by id = " + carId, e);
        }
    }

    private void createNewDriversRelations(Car car) {
        String createRelations = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createRelationsStatement =
                        connection.prepareStatement(createRelations)) {
            createRelationsStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createRelationsStatement.setLong(2, driver.getId());
                createRelationsStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create drivers for car: " + car, e);
        }
    }
}
