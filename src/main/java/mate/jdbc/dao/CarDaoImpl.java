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
        String insertCarRequest = "INSERT cars(model, manufacturer_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(
                        insertCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create car " + car, e);
        }
        insertDriversForCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarRequest = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = 'false'";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet carResultSet = getCarStatement.executeQuery();
            if (carResultSet.next()) {
                car = getCar(carResultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car from DB by id " + id, e);
        }
        if (car == null) {
            return Optional.empty();
        }
        car.setDrivers(getDriversByCarId(id));
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarsRequest = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = 'false'";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet carsResultSet = getAllCarsStatement.executeQuery();
            while (carsResultSet.next()) {
                cars.add(getCar(carsResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = 'false'";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car " + car, e);
        }
        deleteDriversFromCar(car);
        insertDriversForCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = 'true' WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id" + id, e);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllByDriverRequest
                = "SELECT car_id, model, manufacturer_id, manufacturers.name, country "
                + "FROM cars_drivers INNER JOIN cars ON car_id = cars.id "
                + "INNER JOIN drivers ON driver_id = drivers.id "
                + "INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE driver_id = ? "
                + "AND drivers.is_deleted = 'false' AND cars.is_deleted = 'false';";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet getAllByDriverResultSet = getAllByDriverStatement.executeQuery();
            while (getAllByDriverResultSet.next()) {
                cars.add(getCar(getAllByDriverResultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCarId(car.getId()));
        }
        return cars;
    }

    private Car getCar(ResultSet carSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(carSet.getLong(1));
        manufacturer.setName(carSet.getString("name"));
        manufacturer.setCountry(carSet.getString("country"));
        Car car = new Car();
        car.setId(carSet.getObject(1, Long.class));
        car.setModel(carSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void insertDriversForCar(Car car) {
        String insertDriversRequest = "INSERT cars_drivers VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert drivers to car " + car, e);
        }
    }

    private void deleteDriversFromCar(Car car) {
        String deleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement
                        = connection.prepareStatement(deleteDriversRequest)) {
            deleteDriversStatement.setLong(1, car.getId());
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete drivers for car " + car, e);
        }
    }

    private List<Driver> getDriversByCarId(Long id) {
        String getDriversRequest = "SELECT driver_id, name, license_number "
                + "FROM cars_drivers INNER JOIN drivers ON driver_id = drivers.id "
                + "WHERE car_id = ? AND drivers.is_deleted = 'false'";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet driversSet = getDriversStatement.executeQuery();
            while (driversSet.next()) {
                drivers.add(getDriver(driversSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers from DB for car_id " + id, e);
        }
        return drivers;
    }

    private Driver getDriver(ResultSet driversSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(driversSet.getLong(1));
        driver.setName(driversSet.getString("name"));
        driver.setLicenseNumber(driversSet.getString("license_number"));
        return driver;
    }
}
