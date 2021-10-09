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
        String query = "INSERT INTO cars(id_manufacturers, model) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            Long manufacturerId = car.getManufacturer().getId();
            String model = car.getModel();
            createCarStatement.setLong(1, manufacturerId);
            createCarStatement.setString(2, model);
            createCarStatement.executeUpdate();
            ResultSet generatedKey = createCarStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                Long id = generatedKey.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't create the car. Car = " + car, throwables);
        }
        insertDrivers(car);
        return car;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(1, car.getId());
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert drivers to the car. Car = "
                    + car, throwables);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * FROM cars JOIN manufacturers "
                + "ON cars.id_manufacturers = manufacturers.id "
                + "WHERE id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car. Car's id = " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversToCar(car));
        }
        return Optional.ofNullable(car);
    }

    private List<Driver> getDriversToCar(Car car) {
        String query = "SELECT drivers_id, name, license_number FROM drivers "
                + "JOIN cars_drivers cd ON drivers.id = cd.drivers_id "
                + "WHERE cars_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement = connection.prepareStatement(query)) {
            List<Driver> drivers = new ArrayList<>();
            Long carId = car.getId();
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                Long driverId = resultSet.getObject("driver_id", Long.class);
                String driverName = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                driver.setId(driverId);
                driver.setName(driverName);
                driver.setLicenseNumber(licenseNumber);
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all drivers to this car. Car = "
                    + car, throwable);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars JOIN manufacturers "
                + "ON cars.id_manufacturers = manufacturers.id WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all cars. ", throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversToCar(car));
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        car.setModel(model);
        car.setId(id);
        Manufacturer manufacturer = new Manufacturer();
        String manufacturerName = resultSet.getString("name");
        String country = resultSet.getString("country");
        Long manufacturersId = resultSet.getObject("manufacturersId", Long.class);
        manufacturer.setName(manufacturerName);
        manufacturer.setCountry(country);
        manufacturer.setId(manufacturersId);
        car.setManufacturer(manufacturer);
        return car;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ? , id_manufacturers = ?"
                + "WHERE id = ? AND is_deleted = FALSE";
        removeConnectionWithCarAndDriver(car);
        addConnectionWithCarAndDriver(car);
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars_id FROM cars JOIN cars_drivers cd on cars.id = cd.cars_id "
                + "WHERE drivers_id = ? AND cars.is_deleted = FALSE;";
        List<Long> carsId = new ArrayList<>();
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsToDriverStatement
                        = connection.prepareStatement(query)) {
            getAllCarsToDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsToDriverStatement.executeQuery();
            while (resultSet.next()) {
                Long carId = resultSet.getObject("cars_id", Long.class);
                carsId.add(carId);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all car for this driver. Driver id = "
                    + driverId, throwable);
        }
        for (Long carId : carsId) {
            Car car = get(carId).orElseThrow(() ->
                    new DataProcessingException("Can't get car from DB by id = " + carId));
            cars.add(car);
        }
        return cars;
    }

    public void addConnectionWithCarAndDriver(Car car) {
        String query = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addConnectionStatement = connection.prepareStatement(query)) {
            addConnectionStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addConnectionStatement.setLong(2, driver.getId());
                addConnectionStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't add connection with car and drivers. Car = "
                    + car, throwable);
        }
    }

    public void removeConnectionWithCarAndDriver(Car car) {
        String query = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeConnectionStatement = connection.prepareStatement(query)) {
            removeConnectionStatement.setLong(1, car.getId());
            removeConnectionStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't remove connection with car and drivers. "
                    + "Car = " + car, throwable);
        }
    }
}
