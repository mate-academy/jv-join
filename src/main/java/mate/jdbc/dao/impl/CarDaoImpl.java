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
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car: "
                    + car, throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT cars.id, manufacturers.name, model, country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.id = manufacturers.id "
                + "WHERE cars.is_deleted = false AND cars.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = carParser(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDriver(getDriversForCars(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT cars.id, model, manufacturers.name,"
                + "manufacturers.country, cars.is_deleted FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                Car car = carParser(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        setDriversToCars(cars);
        return cars;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        if (driverId == null) {
            throw new RuntimeException("Invalid driver id: " + driverId);
        }
        String getAllByDriverRequest = "SELECT * FROM cars_drivers "
                                + "JOIN cars ON cars_drivers.car_id = cars.id JOIN manufacturers "
                                + "ON cars.manufacturer_id = manufacturers.id "
                                + "WHERE driver_id = ? AND cars.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getByDriversStatement =
                        connection.prepareStatement(getAllByDriverRequest)) {
            getByDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getByDriversStatement.executeQuery();
            while (resultSet.next()) {
                Car car = carParser(resultSet);
                cars.add(car);
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars from DB by driver id. "
                    + driverId, throwable);
        }
        setDriversToCars(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(updateRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB.", throwable);
        }
        removeDriversRelations(car);
        List<Driver> drivers = car.getDriver();
        if (drivers != null) {
            for (Driver driver : drivers) {
                addDriverRelation(car, driver);
            }
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    private Car carParser(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        manufacturer.setId(resultSet.getObject("id",Long.class));
        manufacturer.setName(resultSet.getString("manufacturers.name"));
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("cars.model"));
        car.setId(resultSet.getObject("id",Long.class));
        return car;
    }

    private List<Driver> getDriversForCars(Long id) {
        String getAllDriversRequest = "SELECT drivers.id, name, license_number "
                + "FROM drivers JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ?";
        List<Driver> allDriversList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getAllDriversRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                allDriversList.add(parseDrivers(resultSet));
            }
            return allDriversList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    private Driver parseDrivers(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setName(resultSet.getString("name"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement
                        = connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            if (car.getDriver() != null) {
                for (Driver driver : car.getDriver()) {
                    insertDriversStatement.setLong(2, driver.getId());
                    insertDriversStatement.executeUpdate();
                }
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert drivers to car: "
                    + car, throwable);
        }
    }

    private void removeDriversRelations(Car car) {
        String removeRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeStatement
                        = connection.prepareStatement(removeRequest)) {
            removeStatement.setLong(1, car.getId());
            removeStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't remove driver relations with car: "
                    + car, throwable);
        }
    }

    private void addDriverRelation(Car car, Driver driver) {
        String insertDriverRelationRequest
                = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement
                        = connection.prepareStatement(insertDriverRelationRequest)) {
            insertStatement.setLong(1, car.getId());
            insertStatement.setLong(2, driver.getId());
            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't add driver relations with car: "
                    + car, throwable);
        }
    }

    private void setDriversToCars(List<Car> cars) {
        if (cars.size() != 0) {
            for (Car car : cars) {
                car.setDriver(getDriversForCars(car.getId()));
            }
        }
    }
}
