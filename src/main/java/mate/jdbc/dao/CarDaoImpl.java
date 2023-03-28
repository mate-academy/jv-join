package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
        String insertCarRequest = "INSERT INTO cars(model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(
                                insertCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setObject(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert car " + car + " into DB", e);
        }
        insertDriversOfTheCar(car);
        return car;
    }

    private void insertDriversOfTheCar(Car car) {
        String insertDriversRequest =
                "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriverStatement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(1, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Couldn't insert driver for the car" + car, e);
        }

    }

    @Override
    public Car get(Long id) {
        Car car = null;
        String getCarByIdRequest = "SELECT c.id, c.model, c.manufacturer_id, m.name, m.country \n"
                + "FROM cars c JOIN manufacturers m\n"
                + "ON c.manufacturer_id = m.id\n"
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection =
                     ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getCarByIdRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSetWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    private Car parseCarFromResultSetWithManufacturer(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("manufacturer_id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversForCarRequest =
                "SELECT id, name, licenseNumber FROM drivers d"
                        + " JOIN cars_drivers cd ON d.id = cd.driver_id"
                        + " WHERE cd.car_id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement =
                        connection.prepareStatement(getDriversForCarRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> driversList = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver(resultSet.getObject("id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("licenseNumber"));
                driversList.add(driver);
            }
            return driversList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for car id + " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        List<Car> carsList = new ArrayList<>();
        String getAllCarsRequest = "SELECT *, m.name, m.country FROM cars c\n"
                + "JOIN manufacturers m ON c.manufacturer_id = m.id\n"
                + "WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement =
                        connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarFromResultSetWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get List of cars from database", e);
        }
        carsList.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return carsList;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't update the car in the cars table " + car, e);
        }
        try {
            deleteCarsDriversRelations(car);
            getDriversForCar(car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't update relations in cars_drivers table for car" + car, e);
        }
        return car;
    }

    private void deleteCarsDriversRelations(Car car) throws SQLException {
        String deleteCarDriverRelationRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        Connection connection = ConnectionUtil.getConnection();
        PreparedStatement deleteCarDriverRelationStatement =
                connection.prepareStatement(deleteCarDriverRelationRequest);
        deleteCarDriverRelationStatement.setLong(1, car.getId());
        deleteCarDriverRelationStatement.executeUpdate();
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteRequest)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car from database with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> carsList = new ArrayList<>();
        String getDriversRequest =
                "SELECT *, c.model, c.manufacturer_id FROM cars_drivers cd "
                        + "INNER JOIN cars c ON cd.car_id = c.id WHERE driver_id = ?"
                        + " AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getDriversRequest)) {
            getAllDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(get(resultSet.getLong("car_id")));
            }
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get all the cars by driver id " + driverId, e);
        }
        return carsList;
    }
}
