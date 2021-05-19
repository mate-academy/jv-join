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
        String insertQuery = "insert into cars (model, manufacturer_id) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement = connection.prepareStatement(insertQuery,
                         Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        return insertCarDrivers(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "select c.id, c.model, c.manufacturer_id, m.name, m.country from cars c "
                + "join manufacturers m on manufacturer_id = m.id "
                + "where c.id = ? and c.deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getAllCarDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "select c.id, c.model, c.manufacturer_id, m.name, m.country "
                + "from cars c join manufacturers m on manufacturer_id = m.id "
                + "where c.deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement
                         = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            Car car;
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getAllCarDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverQuery = "select c.id, c.model, c.manufacturer_id, m.name,"
                + " m.country, cd.driver_id from cars c "
                + "join manufacturers m on manufacturer_id = m.id "
                + "join cars_drivers cd on car_id = c.id "
                + "join drivers d on cd.driver_id = d.id "
                + "where driver_id = ? and c.deleted = false and d.deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement
                         = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            Car car;
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars by Driver id "
                    + driverId, throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getAllCarDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "update cars "
                + "set model = ?, manufacturer_id = ? "
                + "where id = ? and deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement
                         = connection.prepareStatement(updateQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        removeCarDrivers(car.getId());
        return insertCarDrivers(car);
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement = connection.prepareStatement(deleteQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    private void removeCarDrivers(Long id) {
        String deleteQuery = "delete from cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteRelationStatement
                         = connection.prepareStatement(deleteQuery)) {
            deleteRelationStatement.setLong(1, id);
            deleteRelationStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete relation with id " + id, throwable);
        }
    }

    private Car insertCarDrivers(Car car) {
        String insertQuery = "insert into cars_drivers (car_id, driver_id) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement = connection.prepareStatement(insertQuery)) {
            saveCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                saveCarStatement.setLong(2, driver.getId());
                saveCarStatement.executeUpdate();
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create relations with "
                    + car + ". ", throwable);
        }
    }

    private List<Driver> getAllCarDrivers(Long carId) {
        String getDriversQuery = "select c.car_id, d.id as driver_id, d.name, d.license_number "
                + "from cars_drivers c join drivers d on c.driver_id = d.id where c.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getDriversQuery)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + carId, throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        String model = resultSet.getString("model");
        Long carId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(manufacturerId);
        Car car = new Car(model, manufacturer);
        car.setId(carId);
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }
}
