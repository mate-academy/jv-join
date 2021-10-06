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
        String insertCarRequest = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(insertCarRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1,car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject("id", Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot create car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarByIdRequest = "SELECT cars.id, model, manufacturer_id,"
                + " m.id, m.name, m.country "
                + "FROM cars "
                + "JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getCarByIdRequest)) {
            getAllCarsStatement.setLong(1, id);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get car from taxi_db by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE taxi_db.cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                     .prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update car: " + car, e);
        }
        deleteCarIdFromCarsDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE "
                + "WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection
                        .prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            int numbersOfDeletedRows = deleteCarStatement.executeUpdate();
            return numbersOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete car from taxi_db"
                    + "by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT cars.id, model, manufacturer_id, m.id, m.name, m.country "
                + "FROM cars "
                + "JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id "
                + "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(selectRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all cars from taxi_db", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectRequest = "SELECT cars.id, model, manufacturer_id, m.name, m.country "
                + "FROM cars "
                + "JOIN cars_drivers cd "
                + "ON cd.car_id = cars.id "
                + "JOIN manufacturers m "
                + "ON cars.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND cars.is_deleted = FALSE;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement = connection
                        .prepareStatement(selectRequest)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all cars from taxi_db by id: "
                    + driverId, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("cars.id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("m.name"));
        manufacturer.setCountry(resultSet.getString("m.country"));
        car.setManufacturer(manufacturer);
        car.setDrivers(getDriversForCar(car.getId()));
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversRequest = "SELECT id, name, license_number  "
                + "FROM drivers "
                + "JOIN cars_drivers cd "
                + "ON drivers.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection
                        .prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get driver from taxi_db by car_id: "
                    + carId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addDriverToCarStatement = connection
                     .prepareStatement(insertDriversRequest)) {
            addDriverToCarStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(1, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot insert drivers to car: " + car, e);
        }
    }

    private void deleteCarIdFromCarsDrivers(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarIdStatement = connection
                     .prepareStatement(deleteRequest)) {
            deleteCarIdStatement.setLong(1, car.getId());
            deleteCarIdStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update car: " + car, e);
        }
    }
}
