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
        String insertRequest = "INSERT INTO taxi_service_db.cars "
                + "(cars.model, cars.manufactures_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertCarStatement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setLong(2, car.getManufacturer().getId());
            insertCarStatement.executeUpdate();
            ResultSet generatedKeys = insertCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT cars.id, cars.model, "
                + "cars.manufactures_id, manufacturers.name, "
                + "manufacturers.country FROM taxi_service_db.cars "
                + "JOIN taxi_service_db.manufacturers "
                + "ON cars.manufactures_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement
                        = connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarsFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT cars.id, cars.model, "
                + "cars.manufactures_id, manufacturers.name, "
                + "manufacturers.country FROM taxi_service_db.cars "
                + "JOIN taxi_service_db.manufacturers "
                + "ON cars.manufactures_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars "
                    + "from manufacturers table. ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE taxi_service_db.cars SET "
                + "cars.model = ?, cars.manufactures_id = ? "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
            insertDrivers(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update "
                    + car + " in cars DB.", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT cars.id, cars.model, cars.manufactures_id, "
                + "drivers.id, "
                + "drivers.name, drivers.licenseNumber, manufacturers.name, "
                + "manufacturers.country FROM taxi_service_db.cars_drivers "
                + "JOIN taxi_service_db.cars "
                + "ON cars.id = cars_drivers.car_id "
                + "JOIN taxi_service_db.drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "JOIN taxi_service_db.manufacturers "
                + "ON cars.manufactures_id = manufacturers.id "
                + "WHERE cars_drivers.driver_id = ? "
                + "AND drivers.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement
                        = connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a list of cars for driver id "
                    + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO taxi_service_db.cars_drivers "
                + "(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement
                        = connection.prepareStatement(insertDriversRequest)) {
            insertDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(2, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver to the car " + car, e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversForCarRequest = "SELECT drivers.id, drivers.name, drivers.licenseNumber "
                + "FROM taxi_service_db.cars_drivers "
                + "JOIN taxi_service_db.drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE taxi_service_db.cars_drivers.car_id = ? "
                + "AND taxi_service_db.drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverStatement
                        = connection.prepareStatement(getDriversForCarRequest)) {
            getDriverStatement.setLong(1, id);
            ResultSet resultSet = getDriverStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver from DB by id " + id, e);
        }
    }

    private Manufacturer parseManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufactures_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }

    private Car parseCarsFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(parseManufacturerFromResultSet(resultSet));
        car.setId(resultSet.getObject("id",Long.class));
        return car;
    }
}
