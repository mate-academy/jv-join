package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car. " + car, e);
        }

        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id, c.model, m.id as manufacturer_id, m.name, m.country"
                + " FROM taxi.cars c "
                + "JOIN taxi.manufacturers m "
                + "ON c.manufacturer_id = m.id where c.id = ? and c.is_deleted = false;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getStatement =
                            connection.prepareStatement(selectRequest)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithDriverResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a car with id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT c.id, c.model, m.id as manufacturer_id, m.name, m.country"
                + " FROM taxi.cars c "
                + "JOIN taxi.manufacturers m "
                + "ON c.manufacturer_id = m.id where c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement =
                        connection.prepareStatement(selectRequest)) {
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithDriverResultSet(resultSet);
                if (car != null) {
                    car.setDrivers(getDriversForCar(car.getId()));
                }
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't all get from DB ", e);
        }

        return cars;
    }

    @Override
    public Car update(Car car) {
        // 1 оновити books fields
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        int updatedRows;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement updateStatement =
                            connection.prepareStatement(updateCarRequest)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updatedRows = updateStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        if (updatedRows > 0) {
            deleteRelationCarDriver(car);
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE taxi.cars SET is_deleted = true where id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement deleteStatement =
                            connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete a car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT * FROM cars c "
                + "JOIN taxi.cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllCarsByDriverStatement =
                            connection.prepareStatement(query)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                car.setModel(resultSet.getString("model"));
                Long manufactureId = resultSet.getObject("manufacturer_id", Long.class);
                Manufacturer manufacturer = getManufacturerForCar(manufactureId);
                car.setManufacturer(manufacturer);
                car.setId(resultSet.getObject("manufacturer_id", Long.class));
                if (car != null) {
                    car.setDrivers(getDriversForCar(car.getId()));
                }
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list of car by driver id " + driverId, e);
        }
        return cars;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement =
                        connection.prepareStatement(query)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers to car " + car, e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getAllDriversRequest = "SELECT id, name, license_number \n"
                + "FROM taxi.drivers d\n"
                + "JOIN taxi.cars_drivers cd\n"
                + "ON d.id = cd.driver_id where cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversRequest)) {
            getAllDriversStatement.setLong(1, id);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find drivers in DB by car id " + id, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car parseCarWithDriverResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("manufacturer_id", Long.class));
        return car;
    }

    private void deleteRelationCarDriver(Car car) {
        String updateRelationCarDriver = "DELETE FROM cars_drivers cd WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateRelationCarDriverStatement =
                        connection.prepareStatement(updateRelationCarDriver)) {
            updateRelationCarDriverStatement.setLong(1, car.getId());
            updateRelationCarDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car driver relation " + car, e);
        }
    }

    private Manufacturer getManufacturerForCar(Long manufactureId) {
        String query = "SELECT * FROM taxi.manufacturers m where m.id = ?;";
        Manufacturer manufacturer = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getManufacturerForCarStatement =
                        connection.prepareStatement(query)) {
            getManufacturerForCarStatement.setLong(1, manufactureId);
            ResultSet resultSet = getManufacturerForCarStatement.executeQuery();
            if (resultSet.next()) {
                manufacturer = new Manufacturer();
                manufacturer.setId(resultSet.getObject("id", Long.class));
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find manufacturer of car manufacture id "
                    + manufactureId, e);
        }
        return manufacturer;
    }
}
