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
        String insertCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarStatement = connection.prepareStatement(insertCarQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setLong(2, car.getManufacturer().getId());
            insertCarStatement.executeUpdate();
            ResultSet resultSet = insertCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert Car in cars table " + car, e);
        }
        insertCarsAndDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarByIdQuery = "SELECT * FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement
                        = connection.prepareStatement(getCarByIdQuery)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarAndManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get Car from cars DB by Id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriverListFromResultSet(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarsQuery = "SELECT * FROM cars c JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id WHERE c.is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarAndManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all Cars from DB", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriverListFromResultSet(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET (model, manufacturer_id) VALUES (?, ?) "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection.prepareStatement(updateCarQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update Car " + car, e);
        }
        deleteCarsDrivers(car);
        insertCarsAndDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteStatement = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteStatement)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete Car from DB by id " + id, e);
        }

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllCarsByDriverQuery = "SELECT * FROM cars_drivers cd JOIN cars c ON cd.car_id ="
                + " c.id  JOIN manufacturers m ON c.manufacturer_id = m.id WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement
                        = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarAndManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all Cars by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriverListFromResultSet(car.getId()));
        }
        return cars;
    }

    private void insertCarsAndDrivers(Car car) {
        String insertCarDriverQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement insertCarDriverStatement
                        = connection.prepareStatement(insertCarDriverQuery)) {
            insertCarDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertCarDriverStatement.setLong(2, driver.getId());
                insertCarDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert Cars DriverList to cars_drivers table "
                    + car, e);
        }
    }

    private Car getCarAndManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(resultSet.getString("name"),
                resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("m.id", Long.class));
        return new Car(resultSet.getObject("id", Long.class),
                resultSet.getString("model"), manufacturer);
    }

    private List<Driver> getDriverListFromResultSet(Long id) {
        String getDriversQuery = "SELECT * FROM cars_drivers cd JOIN drivers d ON "
                + "cd.driver_id = d.id  WHERE car_id = ? AND d.is_deleted = false;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getDriversQuery)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver(resultSet.getString("name"),
                        resultSet.getString("license_number"));
                driver.setId(resultSet.getObject("id", Long.class));
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create DriverList by Car id: " + id, e);
        }
        return drivers;
    }

    private void deleteCarsDrivers(Car car) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete Car and Driver from cars_drivers "
                    + car, e);
        }
    }
}
