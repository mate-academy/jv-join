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
        String query = "INSERT INTO cars (name, manufacturer_id) "
                + " VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getName());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car. " + car + " ",
                    throwable);
        }
        for (Driver driver : car.getDrivers()) {
            addDriverRelationForCar(car, driver);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id as car_id, c.name as car_name, m.name as manufacturer_name, "
                + "m.id as manufacturer_id, m.country as manufacturer_country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get car from DB by id " + id + " ",
                    throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id as car_id, c.name as car_name, m.name as manufacturer_name, "
                + "m.id as manufacturer_id,"
                + " country as manufacturer_country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCars = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCars.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all cars from DB by id ",
                    throwable);
        }
        parseDriversToListOfCars(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET name = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setString(1, car.getName());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB.", throwable);
        }
        removeDriversRelations(car);
        List<Driver> drivers = car.getDrivers();
        if (drivers != null) {
            for (Driver driver : drivers) {
                addDriverRelationForCar(car, driver);
            }
        }
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            int numberOfDeletedRows = statement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + carId, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT c.name as car_name, c.id as car_id, "
                + "m.name as manufacturer_name, m.country as manufacturer_country, "
                + "m.id as manufacturer_id  from cars_drivers cd "
                + "LEFT JOIN cars c ON cd.car_id = c.id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByDriverStatement
                        = connection.prepareStatement(getAllByDriverRequest)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get cars by drivers id " + driverId,
                    throwable);
        }
        parseDriversToListOfCars(cars);
        return cars;
    }

    private void removeDriversRelations(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeStatement = connection.prepareStatement(deleteRequest)) {
            removeStatement.setLong(1, car.getId());
            removeStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete drivers dependencies for car with id "
                    + car.getId(), throwable);
        }
    }

    private void addDriverRelationForCar(Car car, Driver driver) {
        String insertDriverRelation
                = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement
                        = connection.prepareStatement(insertDriverRelation)) {
            insertStatement.setLong(1, car.getId());
            insertStatement.setLong(2, driver.getId());
            insertStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete drivers dependencies for car with id "
                    + car.getId(), throwable);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT d.id as driver_id, d.name as driver_name, "
                + "d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement
                        = connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get drivers for car with id " + carId,
                    throwable);
        }
    }

    private void parseDriversToListOfCars(List<Car> cars) {
        if (cars.size() != 0) {
            for (Car car : cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
    }

    private Car getCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setName(resultSet.getString("car_name"));
        car.setId(resultSet.getObject("car_id", Long.class));
        Manufacturer manufacturer
                = new Manufacturer(resultSet.getString("manufacturer_name"),
                resultSet.getString("manufacturer_country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("driver_name"),
                resultSet.getString("license_number"));
        driver.setId(resultSet.getObject("driver_id", Long.class));
        return driver;
    }
}
