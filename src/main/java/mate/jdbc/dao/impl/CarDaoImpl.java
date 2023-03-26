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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(insertRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t create new car = " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarById = "SELECT cars.id, cars.model, cars.manufacturer_id, manufacturers.name,"
                + " manufacturers.country "
                + "FROM taxi_service.cars JOIN taxi_service.manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarById)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = createCarByResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get car by id = " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCars = "SELECT cars.id, cars.model, cars.manufacturer_id,"
                + " manufacturers.name, manufacturers.country "
                + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(getAllCars)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(createCarByResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all car from DB", e);
        }
        carList.forEach(c -> c.setDrivers(getDriversByCarId(c.getId())));
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateCar = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(updateCar)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car = " + car, e);
        }
        deleteRelation(car.getId());
        createRelation(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCar = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(deleteCar)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car by id = " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAll = "SELECT * FROM taxi_service.cars JOIN taxi_service.cars_drivers "
                + "ON taxi_service.cars.id = taxi_service.cars_drivers.car_id "
                + "JOIN taxi_service.manufacturers "
                + "ON taxi_service.cars.manufacturer_id = taxi_service.manufacturers.id "
                + "WHERE taxi_service.cars_drivers.driver_id = ?;";
        List<Car> allCar = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarByDriverStatement =
                        connection.prepareStatement(getAll)) {
            getAllCarByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarByDriverStatement.executeQuery();
            while (resultSet.next()) {
                allCar.add(createCarByResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars by driver id = " + driverId, e);
        }
        allCar.forEach(c -> c.setDrivers(getDriversByCarId(c.getId())));
        return allCar;
    }

    private Car createCarByResultSet(ResultSet resultSet) throws SQLException {
        String model = resultSet.getString("model");
        Long carId = resultSet.getObject("id", Long.class);
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer =
                new Manufacturer(manufacturerName, manufacturerCountry, manufacturerId);
        return new Car(carId, model, manufacturer);
    }

    private List<Driver> getDriversByCarId(Long carId) {
        String getDrivers = "SELECT drivers.id, drivers.name, drivers.license_number "
                + "FROM taxi_service.drivers "
                + "JOIN cars_drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ?;";
        List<Driver> driverList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(getDrivers)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driverList.add(createDriverByResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers by car id = " + carId, e);
        }
        return driverList;
    }

    private Driver createDriverByResultSet(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("id", Long.class);
        String driverName = resultSet.getString("name");
        String driverLicenseNumber = resultSet.getString("license_number");
        return new Driver(driverId, driverName, driverLicenseNumber);
    }

    private void deleteRelation(Long carId) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationStatement =
                        connection.prepareStatement(deleteQuery)) {
            deleteRelationStatement.setLong(1, carId);
            deleteRelationStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can`t delete relationship in cars_drivers by car_id = " + carId, e);
        }
    }

    private void createRelation(Car car) {
        String createQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createRelationStatement =
                        connection.prepareStatement(createQuery)) {
            createRelationStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createRelationStatement.setLong(2, driver.getId());
                createRelationStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can`t create relationship in cars_drivers by car_id = " + car.getId(), e);
        }
    }
}
