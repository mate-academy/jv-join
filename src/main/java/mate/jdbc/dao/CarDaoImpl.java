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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement
                        = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't create a car " + car + " ", throwables);
        }
        insertDrivers(car);
        getDriversForCar(car.getId());
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + "m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getCarStatement =
                            connection.prepareStatement(selectRequest,
                                    Statement.RETURN_GENERATED_KEYS)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get a car by Id " + id + " ", throwables);
        }
        if (car != null) {
            car.setDriverList(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, m.name, m.country"
                + " FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> carsList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table. ", throwable);
        }
        carsList.forEach(car -> car.setDriverList(getDriversForCar(car.getId())));
        return carsList;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement preparedStatement =
                            connection.prepareStatement(updateRequest)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update a car " + car, throwable);
        }
        deleteDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement preparedStatement =
                            connection.prepareStatement(deleteRequest)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a driver by id " + id + " ",
                    throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest
                = "SELECT * FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "JOIN drivers d ON d.id = cd.driver_id WHERE cd.driver_id = ? "
                + "AND c.is_deleted = FALSE AND d.is_deleted = FALSE;";
        List<Car> carsList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        getAllByDriverRequest)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                carsList.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get a list of cars by driver Id "
                    + driverId, throwables);
        }
        carsList.forEach(car -> car.setDriverList(getDriversForCar(car.getId())));
        return carsList;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement =
                        connection.prepareStatement(insertDriversRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert driver to a car "
                    + car + " ", throwables);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM \n"
                + "drivers d JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't find drivers for a car by car Id "
                    + carId + " ", throwables);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void deleteDrivers(Long id) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(deleteRequest)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can not delete drivers with car id = " + id, e);
        }
    }
}
