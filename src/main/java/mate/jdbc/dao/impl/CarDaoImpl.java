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
        String query = "INSERT INTO cars (manufacturer_id, model) values (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement creatStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            creatStatement.setLong(1, car.getManufacturer().getId());
            creatStatement.setString(2, car.getModel());
            creatStatement.executeUpdate();
            ResultSet generatedKeys = creatStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: " + car, e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, cars.manufacturer_id, cars.model, manufacturers.name, "
                + "manufacturers.country FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = false";
        Car car = new Car();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatment = connection.prepareStatement(query)) {
            getStatment.setLong(1, id);
            ResultSet resultSet = getStatment.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        List<Driver> driversFromCar = getDriversFromCar(id);
        car.setDrivers(driversFromCar);
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.manufacturer_id, cars.model, "
                + "manufacturers.id, manufacturers.name, manufacturers.country FROM cars "
                + "INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id AND cars.is_deleted = false;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatment = connection.prepareStatement(query)) {

            ResultSet resultSet = getAllStatment.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars list", e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversFromCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET cars.model = ?,"
                + "cars.manufacturer_id = ? WHERE cars.id = ? AND cars.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatment = connection.prepareStatement(query)) {
            updateStatment.setString(1, car.getModel());
            updateStatment.setLong(2, car.getManufacturer().getId());
            updateStatment.setLong(3, car.getId());
            updateStatment.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        removeDriversFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET cars.is_deleted = true "
                + "WHERE cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatment = connection.prepareStatement(query)) {
            deleteStatment.setLong(1, id);
            return (deleteStatment.executeUpdate() >= 1);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT drivers.id, drivers.name, drivers.license_number, "
                + "cars_drivers.car_id "
                + "FROM drivers "
                + "INNER JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE driver_id = ?;";
        List<Long> carsID = new ArrayList<>();
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Long carID = resultSet.getObject("cars_drivers.car_id", Long.class);
                carsID.add(carID);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars by driver id " + driverId, e);
        }
        for (Long carID : carsID) {
            carList.add(get(carID).get());
        }
        return carList;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("cars.id", Long.class));
        car.setModel(resultSet.getString("cars.model"));
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("manufacturers.name"),
                resultSet.getString("manufacturers.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void addDriversToCar(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection.prepareStatement(query)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car " + car, e);
        }
    }

    private void removeDriversFromCar(Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversFromCarStatement
                        = connection.prepareStatement(query)) {
            removeDriversFromCarStatement.setLong(1, car.getId());
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove drivers from car " + car, e);
        }
    }

    private List<Driver> getDriversFromCar(Long id) {
        String query = "SELECT drivers.id, drivers.name, drivers.license_number "
                + "FROM cars_drivers "
                + "INNER JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id  "
                + "WHERE drivers.is_deleted = false "
                + "AND cars_drivers.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversFromCarStatement = connection.prepareStatement(query)) {
            getDriversFromCarStatement.setLong(1, id);
            ResultSet resultSet = getDriversFromCarStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver(resultSet.getObject("drivers.id", Long.class),
                        resultSet.getString("drivers.name"),
                        resultSet.getString("drivers.license_number"));
                driverList.add(driver);
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers from car with id " + id, e);
        }
    }
}
