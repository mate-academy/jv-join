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
                PreparedStatement createCarStatement = connection.prepareStatement(
                        createQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to create car " + car, e);
        }
        if (car.getDrivers() != null) {
            insertDriversForCar(car);
        } else {
            car.setDrivers(new ArrayList<>());
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, name, country"
                + " FROM cars c"
                + " JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getAllByCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, name, country"
                + " FROM cars c"
                + " JOIN manufacturers m"
                + " ON c.manufacturer_id = m.id"
                + " WHERE c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to get all cars", e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllByCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars "
                + " SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(updateQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            if (updateCarStatement.executeUpdate() < 1) {
                throw new DataProcessingException("No data update was performed at car " + car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement"
                    + " to update car with id " + car.getId(), e);
        }
        deleteDriversOfCar(car);
        insertDriversForCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(deleteQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement"
                    + " to delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getQuery = "SELECT c.id, c.model, m.id AS manufacturer_id, m.name, m.country"
                + " FROM cars c"
                + " JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " JOIN cars_drivers cd ON c.id = cd.car_id"
                + " WHERE cd.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsForDriverStatement =
                        connection.prepareStatement(getQuery)) {
            getCarsForDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsForDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to get cars for driver by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getAllByCar(car.getId()));
        }
        return cars;
    }

    @Override
    public List<Driver> getAllByCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getQuery = "SELECT d.id, d.name, d.licence_number"
                + " FROM drivers d"
                + " JOIN cars_drivers cd"
                + " ON d.id = cd.driver_id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement =
                        connection.prepareStatement(getQuery)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to get drivers for car by car id " + carId, e);
        }
    }

    private void insertDriversForCar(Car car) {
        String createQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarAndDriverStatement =
                        connection.prepareStatement(createQuery)) {
            insertCarAndDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertCarAndDriverStatement.setLong(2, driver.getId());
                insertCarAndDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to insert car and it's drivers relationship" + car, e);
        }
    }

    private void deleteDriversOfCar(Car car) {
        String createQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarAndDriverStatement =
                        connection.prepareStatement(createQuery)) {
            deleteCarAndDriverStatement.setLong(1, car.getId());
            deleteCarAndDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't execute statement "
                    + "to delete car and it's drivers relationship " + car, e);
        }
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject(1, Long.class));
        car.setModel(resultSet.getString(2));

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(3, Long.class));
        manufacturer.setName(resultSet.getString(4));
        manufacturer.setCountry(resultSet.getString(5));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject(1, Long.class));
        driver.setName(resultSet.getString(2));
        driver.setLicenceNumber(resultSet.getString(3));
        return driver;
    }
}
