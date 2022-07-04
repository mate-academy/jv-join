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
    private Connection connection = ConnectionUtil.getConnection();

    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car + ".", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String getCarRequest = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (PreparedStatement getCarStatement = connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car from taxi_service_db "
                    + "by id " + id + ". ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement getAllCarsStatement
                     = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of cars from cars table", e);
        }
        return setDriversForCars(cars);
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ?;";
        try (PreparedStatement updateCarStatement
                     = connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update cat " + car + ". ", e);
        }
        deleteRelationsCarWithDrives(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (PreparedStatement deleteCarStatement = connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, carId);
            return deleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't deleted car by id " + carId + ". ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriversRequest
                = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country "
                + "FROM cars c "
                + "JOIN cars_drivers cd ON c.id = cd.cars_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE drivers_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (PreparedStatement getAllCarsByDriversStatement
                     = connection.prepareStatement(getAllCarsByDriversRequest)) {
            getAllCarsByDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriversStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id " + driverId + ". ", e);
        }
        return setDriversForCars(cars);
    }

    private void deleteRelationsCarWithDrives(Long carId) {
        String deleteRelationsRequest = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (PreparedStatement deleteRelationsStatement
                     = connection.prepareStatement(deleteRelationsRequest)) {
            deleteRelationsStatement.setLong(1, carId);
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations car with drivers "
                    + "by car id " + carId, e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long driverId) {
        String getAllDriversRequest = "SELECT id, name, license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.id = cd.drivers_id WHERE cd.cars_id = ?;";
        try (PreparedStatement getAllDriversStatement
                     = connection.prepareStatement(getAllDriversRequest)) {
            getAllDriversStatement.setLong(1, driverId);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver from BD by id " + driverId, e);
        }
    }

    private List<Car> setDriversForCars(List<Car> cars) {
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (cars_id, drivers_id)"
                + " VALUES (?, ?)";
        try (PreparedStatement insertDriversStatement
                     = connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car", e);
        }
    }
}
