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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, car.getModel());
            insertStatement.setLong(2, car.getManufacturer().getId());
            insertStatement.executeUpdate();
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car: " + car + " in DataBase ", e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT cars.id, cars.model, cars.manufacturer_id, manufacturers.name,"
                + " manufacturers.country FROM cars INNER JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id WHERE cars.id = ?"
                + " AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Car by id: " + id + " from DataBase ", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String getAllQuery = "SELECT cars.id, cars.model, cars.manufacturer_id, "
                + "manufacturers.name, manufacturers.country FROM cars "
                + "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DataBase ", e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET cars.model = ?, cars.manufacturer_id = ? "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update Car: " + car + " in DataBase ", e);
        }
        removeDriversFromCar(car);
        addDriversToCar(car);
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
            throw new DataProcessingException("Can't delete Car by id: " + id
                    + " from DataBase ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carList = new ArrayList<>();
        String getAllByDriver = "SELECT cars.id, cars.model, cars.manufacturer_id, "
                + "manufacturers.name, manufacturers.country FROM cars INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "INNER JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriver)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars bt driver id: " + driverId, e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return carList;
    }

    private Car getCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("cars.id", Long.class));
        car.setModel(resultSet.getString("cars.model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("cars.manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturers.name"));
        manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> driverList = new ArrayList<>();
        String allDriversForCarQuery = "SELECT drivers.id, drivers.name, "
                + "drivers.license_number FROM drivers INNER JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversForCarStatement =
                        connection.prepareStatement(allDriversForCarQuery)) {
            getAllDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversForCarStatement.executeQuery();
            while (resultSet.next()) {
                driverList.add(getDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all drivers for car by car id: "
                    + carId, e);
        }
        return driverList;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("drivers.id", Long.class));
        driver.setName(resultSet.getString("drivers.name"));
        driver.setLicenseNumber(resultSet.getString("drivers.license_number"));
        return driver;
    }

    private void addDriversToCar(Car car) {
        String insertDriversToCarQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversToCarStatement =
                        connection.prepareStatement(insertDriversToCarQuery)) {
            insertDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversToCarStatement.setLong(2, driver.getId());
                insertDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers to car: "
                    + car + "in DataBase", e);
        }
    }

    private void removeDriversFromCar(Car car) {
        String removeQuery = "DELETE FROM cars_drivers WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeStatement = connection.prepareStatement(removeQuery)) {
            removeStatement.setLong(1, car.getId());
            removeStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove drivers from car: "
                    + car + "in DataBase ", e);
        }
    }
}
