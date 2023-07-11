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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert data to table cars. Car " + car, e);
        }
        insertCarsDriversConnections(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id car_id"
                + "  ,c.model car_model"
                + "  ,m.id manufacturer_id"
                + "  ,m.name manufacturer_name"
                + "  ,m.country manufacturer_country"
                + "  ,COALESCE(wf.drivers_count, 0) drivers_count"
                + "  ,d.id driver_id"
                + "  ,d.name driver_name"
                + "  ,d.license_number driver_license_number "
                + "FROM cars c "
                + "LEFT JOIN cars_drivers cd ON c.id  = cd.car_id "
                + "LEFT JOIN drivers d ON d.id  = cd.driver_id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "LEFT JOIN (SELECT t1.car_id, count(*) drivers_count "
                + "             FROM cars_drivers t1 "
                + "            INNER JOIN drivers t2 ON t1.driver_id = t2.id "
                + "                                 AND t2.is_deleted = FALSE"
                + "            GROUP BY t1.car_id) wf ON wf.car_id = c.id "
                + "WHERE c.is_deleted = FALSE "
                + "  AND c.id = (?) "
                + "  AND COALESCE(d.is_deleted, FALSE) = FALSE "
                + "  AND COALESCE(m.is_deleted, FALSE) = FALSE;";
        Optional<Car> optionalCar = Optional.empty();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                optionalCar = Optional.of(getCarEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get data from table cars by id "
                    + id, e);
        }
        return optionalCar;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id car_id"
                + "  ,c.model car_model"
                + "  ,m.id manufacturer_id"
                + "  ,m.name manufacturer_name"
                + "  ,m.country manufacturer_country"
                + "  ,COALESCE(wf.drivers_count, 0) drivers_count"
                + "  ,d.id driver_id"
                + "  ,d.name driver_name"
                + "  ,d.license_number driver_license_number "
                + "FROM cars c "
                + "LEFT JOIN cars_drivers cd ON c.id  = cd.car_id "
                + "LEFT JOIN drivers d ON d.id  = cd.driver_id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "LEFT JOIN (SELECT t1.car_id, count(*) drivers_count "
                + "             FROM cars_drivers t1 "
                + "            INNER JOIN drivers t2 ON t1.driver_id = t2.id "
                + "                                 AND t2.is_deleted = FALSE"
                + "            GROUP BY t1.car_id) wf ON wf.car_id = c.id "
                + "WHERE c.is_deleted = FALSE "
                + "  AND COALESCE(d.is_deleted, FALSE) = FALSE "
                + "  AND COALESCE(m.is_deleted, FALSE) = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCarEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get data from table cars", e);
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = (?), manufacturer_id = (?) WHERE id = (?) "
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update data from table cars by "
                    + "Car " + car, e);
        }
        deleteCarsDriversConnections(car);
        insertCarsDriversConnections(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars set is_deleted = TRUE WHERE id = (?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(query)) {
            prepareStatement.setLong(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete (soft delete) data from table cars by "
                    + "id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id car_id"
                + "  ,c.model car_model"
                + "  ,m.id manufacturer_id"
                + "  ,m.name manufacturer_name"
                + "  ,m.country manufacturer_country"
                + "  ,COALESCE(wf.drivers_count, 0) drivers_count"
                + "  ,d.id driver_id"
                + "  ,d.name driver_name"
                + "  ,d.license_number driver_license_number "
                + "FROM cars c "
                + "LEFT JOIN cars_drivers cd ON c.id  = cd.car_id "
                + "LEFT JOIN drivers d ON d.id  = cd.driver_id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "LEFT JOIN (SELECT t1.car_id, count(*) drivers_count "
                + "             FROM cars_drivers t1 "
                + "            INNER JOIN drivers t2 ON t1.driver_id = t2.id "
                + "                                 AND t2.is_deleted = FALSE "
                + "            GROUP BY t1.car_id) wf ON wf.car_id = c.id "
                + "WHERE c.is_deleted = FALSE "
                + "  AND c.id IN (SELECT DISTINCT cd2.car_id "
                + "                 FROM cars_drivers cd2 "
                + "                WHERE cd2.driver_id = (?)) "
                + "  AND COALESCE(d.is_deleted, FALSE) = FALSE "
                + "  AND COALESCE(m.is_deleted, FALSE) = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(query)) {
            prepareStatement.setLong(1, driverId);
            ResultSet resultSet = prepareStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCarEntity(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get data from table cars", e);
        }
        return carList;
    }

    private void deleteCarsDriversConnections(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = (?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement prepareStatement = connection.prepareStatement(query)) {
            prepareStatement.setLong(1, car.getId());
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete data from table cars_drivers by Car "
                    + car, e);
        }
    }

    private void insertCarsDriversConnections(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert data to cars_drivers. Car " + car, e);
        }
    }

    private Car getCarEntity(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String carModel = resultSet.getString("car_model");

        Manufacturer manufacturer = getManufacturerEntity(resultSet);

        int driversCount = resultSet.getInt("drivers_count");
        List<Driver> driverList = new ArrayList<>();
        if (driversCount > 0) {
            driverList.add(getDriverEntity(resultSet));
            for (int i = 1; i < driversCount; i++) {
                resultSet.next();
                driverList.add(getDriverEntity(resultSet));
            }
        }
        return new Car(carId, carModel, manufacturer, driverList);
    }

    private Manufacturer getManufacturerEntity(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("manufacturer_name");
        String manufacturerCountry = resultSet.getString("manufacturer_country");
        return new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
    }

    private Driver getDriverEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("driver_id", Long.class);
        String name = resultSet.getString("driver_name");
        String licenseNumber = resultSet.getString("driver_license_number");
        return new Driver(id, name, licenseNumber);
    }
}
