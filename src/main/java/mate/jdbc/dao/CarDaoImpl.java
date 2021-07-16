package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String createCarQuery = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(createCarQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
        insertCarsAndDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.id, cars.model, man.name, man.country, manufacturer_id, man.id "
                + "FROM cars JOIN manufacturers man ON cars.manufacturer_id = man.id "
                + "WHERE cars.id = ? AND cars.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getInfoAboutDrivers(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, man.name, man.country, manufacturer_id "
                + "FROM cars JOIN manufacturers man ON cars.manufacturer_id = man.id "
                + "WHERE cars.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getInfoAboutDrivers(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET manufacturer_id = ?, model = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteDrivers(car.getId());
        insertCarsAndDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String selectRequest = "SELECT cd.car_id, c.model as model, "
                + "c.manufacturer_id as manufacturer_id, m.name as name, m.country as country, "
                + "c.id, m.id "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversByDriverStatement = connection
                        .prepareStatement(selectRequest)) {
            getAllDriversByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllDriversByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB by driver id: "
                    + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getInfoAboutDrivers(car.getId()));
        }
        return cars;
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        final Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("model"));
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private void insertCarsAndDrivers(Car car) {
        String carsDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarDriverStatement =
                        connection.prepareStatement(carsDriversQuery)) {
            saveCarDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                saveCarDriverStatement.setLong(2, driver.getId());
                saveCarDriverStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
    }

    private List<Driver> getInfoAboutDrivers(Long id) {
        String getDriversQuery = "SELECT id, name, license_number FROM cars_drivers "
                + "JOIN drivers ON cars_drivers.driver_id = drivers.id "
                + "WHERE car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(getDriversQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get info about drivers in car with id "
                    + id + ". ", throwable);
        }
    }

    private void deleteDrivers(Long id) {
        String removeDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversFromCarStatement = connection
                        .prepareStatement(removeDriversRequest)) {
            removeDriversFromCarStatement.setLong(1, id);
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove drivers from car by car_id : "
                    + id, e);
        }
    }
}
