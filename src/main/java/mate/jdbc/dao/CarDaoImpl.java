package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long carId = generatedKeys.getObject(1, Long.class);
                car.setId(carId);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car in DB: " + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getByIdQuery =
                "SELECT c.id AS car_id, model, m.id as manufacturer_id, name, country "
                        + "FROM cars c "
                        + "JOIN manufacturers m "
                        + "ON c.manufacturer_id = m.id "
                        + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement
                        = connection.prepareStatement(getByIdQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by id: " + car + ". ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery =
                "SELECT c.id AS car_id, model, m.id as manufacturer_id, name, country "
                        + "FROM cars c "
                        + "JOIN manufacturers m "
                        + "ON c.manufacturer_id = m.id "
                        + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of drivers from DB.", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarWithManufacturerQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarWithManufacturerStatement
                        = connection.prepareStatement(updateCarWithManufacturerQuery)) {
            updateCarWithManufacturerStatement.setString(1, car.getModel());
            updateCarWithManufacturerStatement.setLong(2, car.getManufacturer().getId());
            updateCarWithManufacturerStatement.setLong(3, car.getId());
            updateCarWithManufacturerStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with id: "
                    + car.getId() + ". ", e);
        }
        deleteRelationsCarsDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String softDeleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement
                        = connection.prepareStatement(softDeleteCarQuery)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + id + ". ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsIdByDriverQuery = "SELECT id "
                + "FROM cars c "
                + "JOIN cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "WHERE driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsIdByDriverStatement
                        = connection.prepareStatement(getAllCarsIdByDriverQuery)) {
            getAllCarsIdByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsIdByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(Car.of(resultSet.getObject(1, Long.class)));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find car in DB by driver id "
                    + driverId + ". ", e);
        }
        return cars.stream()
                .map(c -> get(c.getId())
                        .orElseThrow(() -> new NoSuchElementException("Could not get driver "
                                + "by id = " + c.getId())))
                .collect(Collectors.toList());
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversToCarStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            insertDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversToCarStatement.setLong(2, driver.getId());
                insertDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car: " + car + ". ", e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = Manufacturer.of(resultSet.getObject(3, Long.class),
                resultSet.getString(4), resultSet.getString(5));
        return Car.of(resultSet.getObject(1, Long.class),
                resultSet.getString(2), manufacturer);
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarQuery = "SELECT id, name, license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement
                        = connection.prepareStatement(getAllDriversForCarQuery)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by carId "
                    + carId + ". ", e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        return Driver.of(resultSet.getObject(1, Long.class),
                resultSet.getString(2), resultSet.getString(3));
    }

    private void deleteRelationsCarsDrivers(Car car) {
        String deleteRelationsQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement
                        = connection.prepareStatement(deleteRelationsQuery)) {
            deleteRelationsStatement.setLong(1, car.getId());
            deleteRelationsStatement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers by car id: "
                    + car.getId() + ". ", e);
        }
    }
}
