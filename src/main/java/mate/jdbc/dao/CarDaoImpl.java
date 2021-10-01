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
        String createCarQuery = "INSERT INTO cars (manufacturer_id, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarStatement = connection.prepareStatement(createCarQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setLong(1, car.getManufacturer().getId());
            saveCarStatement.setString(2, car.getModel());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
        createCarsAndDrivers(car);
        return car;
    }

    private void createCarsAndDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement saveCarDriverStatement =
                        connection.prepareStatement(query)) {
            saveCarDriverStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                saveCarDriverStatement.setLong(2, driver.getId());
                saveCarDriverStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT "
                + "taxi.cars.id AS car_id, "
                + "taxi.cars.model AS car_model, "
                + "taxi.cars.manufacturer_id AS man_id, "
                + "taxi.manufacturers.name AS man_name, "
                + "taxi.manufacturers.country AS man_country "
                + "FROM taxi.cars "
                + "JOIN taxi.manufacturers "
                + "ON taxi.cars.manufacturer_id = taxi.manufacturers.id "
                + "WHERE taxi.cars.id = ? AND taxi.cars.is_deleted = 0;";

        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = carParser(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getInfoAboutDrivers(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT "
                + "`taxi`.`cars`.id AS car_id, "
                + "`taxi`.`cars`.model AS car_model, "
                + "`taxi`.`cars`.manufacturer_id AS man_id, "
                + "`taxi`.`manufacturers`.name AS man_name, "
                + "`taxi`.`manufacturers`.country AS man_country "
                + "FROM `taxi`.`cars` "
                + "JOIN `taxi`.`manufacturers` "
                + "ON `taxi`.`cars`.manufacturer_id = `taxi`.`manufacturers`.id "
                + "WHERE `taxi`.`cars`.is_deleted = 0;";

        List<Car> cars = new ArrayList<>();

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();

            while (resultSet.next()) {
                cars.add(carParser(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get all cars", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getInfoAboutDrivers(car.getId()));
        }
        return cars;
    }

    private List<Driver> getInfoAboutDrivers(Long id) {
        String query = "SELECT "
                + "`taxi`.`drivers`.id AS driver_id, "
                + "`taxi`.`drivers`.name AS driver_name, "
                + "`taxi`.`drivers`.license_number AS driver_license_number "
                + "FROM `taxi`.`cars_drivers` "
                + "JOIN `taxi`.`drivers` "
                + "ON `taxi`.`cars_drivers`.driver_id = `taxi`.`drivers`.id "
                + "WHERE `taxi`.`cars_drivers`.car_id = ? AND `taxi`.`drivers`.is_deleted = 0";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get info about drivers in car with id "
                    + id + ". ", throwable);
        }
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("driver_license_number"));
        return driver;
    }

    public boolean deleteDrivers(Long id) {
        String query = "UPDATE taxi.drivers "
                + "SET taxi.drivers.is_deleted = TRUE "
                + "WHERE taxi.drivers.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection.prepareStatement(query)) {
            deleteDriversStatement.setLong(1, id);
            return deleteDriversStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE taxi.cars SET taxi.cars.is_deleted = TRUE WHERE taxi.cars.id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE taxi.cars "
                + "SET taxi.cars.manufacturer_id = ?, taxi.cars.model = ? "
                + "WHERE taxi.cars.id = ? AND taxi.cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteDrivers(car.getId());
        createCarsAndDrivers(car);
        return car;

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT "
                + "taxi.drivers.id AS driver_id, "
                + "taxi.drivers.name AS driver_name, "
                + "taxi.drivers.license_number AS driver_license_number, "
                + "taxi.cars.id AS car_id, "
                + "taxi.cars.model AS car_model, "
                + "taxi.manufacturers.id AS man_id, "
                + "taxi.manufacturers.name AS man_name, "
                + "taxi.manufacturers.country AS man_country "
                + "FROM taxi.cars_drivers "
                + "JOIN taxi.drivers ON taxi.drivers.id = taxi.cars_drivers.driver_id "
                + "JOIN taxi.cars ON taxi.cars.id = taxi.cars_drivers.car_id "
                + "JOIN taxi.manufacturers ON taxi.cars.id = taxi.manufacturers.id "
                + "WHERE taxi.drivers.id = ? AND taxi.drivers.is_deleted = 0";

        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection.prepareStatement(query)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(carParser(resultSet));
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars by driverId "
                    + driverId, throwable);
        }

        for (Car car: cars) {
            car.setDrivers(getInfoAboutDrivers(car.getId()));
        }
        return cars;
    }

    private Car carParser(ResultSet resultSet) throws SQLException {
        final Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("man_id", Long.class));
        manufacturer.setName(resultSet.getString("man_name"));
        manufacturer.setCountry(resultSet.getString("man_country"));
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getString("car_model"));
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }
}


