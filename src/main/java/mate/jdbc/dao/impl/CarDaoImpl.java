package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
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
        String createRequest = "INSERT INTO cars(model, manufacturer_id)"
                   + " values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(createRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setObject(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert " + car + " to DB", e);
        }
        insertDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT cars.id, model, manufacturer_id, "
                          + "manufacturers.id, name, country "
                          + "FROM cars INNER JOIN manufacturers "
                          + "ON manufacturers.id = cars.manufacturer_id "
                          + "WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection
                        .prepareStatement(getRequest)) {
            getCarStatement.setObject(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car id: " + id + " from DB", e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get cars from DB", e);
        }
        cars.forEach(c -> c.setDrivers(getCarDrivers(c.getId())));
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarByDriverRequest = "SELECT cars.id, model, manufacturer_id, name, country "
                + "FROM cars INNER JOIN car_drivers ON id = car_id "
                + "INNER JOIN manufacturers ON manufacturer_id = manufacturers.id "
                + "WHERE driver_id = ? AND cars.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement = connection
                        .prepareStatement(getCarByDriverRequest)) {
            getCarsByDriverStatement.setObject(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get cars from DB", e);
        }
        cars.forEach(c -> c.setDrivers(getCarDrivers(c.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a car " + car, e);
        }
        updateCarDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car id: " + id + " from DB", e);
        }
    }

    private void insertDriversToCar(Car car) {
        String insertDriverRequest = "INSERT INTO car_drivers(car_id, driver_id)"
                + " values(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement = connection
                          .prepareStatement(insertDriverRequest, Statement.RETURN_GENERATED_KEYS)) {
            insertDriverStatement.setObject(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setObject(2, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert driver to car: " + car, e);
        }
    }

    private List<Driver> getCarDrivers(Long id) {
        String getCarDriversRequest = "SELECT drivers.id, name, license_number, car_drivers.car_id "
                + "FROM drivers INNER JOIN car_drivers "
                + "ON drivers.id = car_drivers.driver_id WHERE car_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarDriversStatement = connection
                        .prepareStatement(getCarDriversRequest)) {
            getCarDriversStatement.setObject(1, id);
            ResultSet resultSet = getCarDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get driver from DB by car id: " + id, e);
        }
    }

    private void updateCarDrivers(Car car) {
        String deleteCarDriversRequest = "DELETE FROM car_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarDriversStatement
                        = connection.prepareStatement(deleteCarDriversRequest)) {
            deleteCarDriversStatement.setObject(1, car.getId());
            deleteCarDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update a drivers in car: "
                    + car, e);
        }
        insertDriversToCar(car);
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId, name, country);
        return new Car(id, model, manufacturer, null);
    }
}
