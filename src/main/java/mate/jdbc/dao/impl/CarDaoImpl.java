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
        String createQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection.prepareStatement(createQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        if (car.getDrivers() != null) {
            insertDriversToRelationDb(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String query = "SELECT c.id, c.model, m.id AS manufacturer_id,"
                + " m.name AS manufacturer, m.country "
                + "AS country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id, c.model, m.id AS manufacturer_id,"
                + " m.name AS manufacturer, m.country "
                + "AS country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from cars DB.", e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(updateRequest)) {
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car in DB " + car,e);
        }
        deleteDrivers(car.getId());
        if (car.getDrivers() != null) {
            insertDriversToRelationDb(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long carId) {
        deleteDrivers(carId);
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            softDeleteCarStatement.setLong(1, carId);
            int numberOfDeletedRows = softDeleteCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id: " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Long> carsId = new ArrayList<>();
        String getAllByDriverQuery = "SELECT * FROM cars_drivers WHERE driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverQuery)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                carsId.add(resultSet.getObject("car_id", Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars Id from "
                    + "cars_drivers DB by driver Id: " + driverId, e);
        }
        List<Car> listOfCars = new ArrayList<>();
        for (Long id : carsId) {
            if (id != null) {
                listOfCars.add(get(id).get());
            }
        }
        return listOfCars;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car(resultSet.getObject("id", Long.class),
                resultSet.getString("model"),
                new Manufacturer(resultSet.getObject("manufacturer_id", Long.class),
                        resultSet.getString("manufacturer"),
                        resultSet.getString("country")));
        return car;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversForCarRequest = "SELECT d.id, d.name, d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Cant find drivers in DB by car id: " + carId, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        return new Driver(resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("license_number"));
    }

    private void insertDriversToRelationDb(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (`car_id`, `driver_id`) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            addDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversStatement.setLong(2, driver.getId());
                addDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to cars_drivers DB : "
                    + car, e);
        }
    }

    private void deleteDrivers(Long carId) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, carId);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Error when deleting drivers from cars_drivers DB "
                   + "by car Id: " + carId,e);
        }
    }
}
