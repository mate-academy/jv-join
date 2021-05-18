package mate.jdbc.dao;

import mate.jdbc.lib.Dao;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createCarStatement = connection.prepareStatement(insertQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getQuery = "SELECT cars.id, cars.model, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(getQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriverByCar(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT cars.id, cars.model, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarStatement = connection.prepareStatement(getAllQuery)) {
            ResultSet resultSet = getCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    +  "from cars table. ", throwable);
        }
        setDrivers(cars);
        return cars;
    }


    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateDriverStatement
                     = connection.prepareStatement(query)) {
            updateDriverStatement.setString(1, car.getModel());
            updateDriverStatement.setLong(2, car.getManufacturer().getId());
            updateDriverStatement.setLong(3, car.getId());
            updateDriverStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
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
        String getAllByDriverQuery = "SELECT cars.id, cars.model, "
                + "manufacturers.name, manufacturers.country "
                + "FROM cars_drivers JOIN cars"
                + "ON cars.id = cars_drivers.car_id "
                + "JOIN manufacturers ON cars.manufacturers_id = manufacturers.id"
                + "WHERE driver_id = ? AND cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarsStatement = connection.prepareStatement(getAllByDriverQuery)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    +  "from cars table. ", throwable);
        }
        setDrivers(cars);
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturers.id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        Long carId = resultSet.getObject("cars.id", Long.class);
        String carModel = resultSet.getString("model");
        Car car = new Car(carModel, manufacturer);
        car.setId(carId);
        return car;
    }

    private List<Driver> getDriverByCar(Long id) {
        String getDriverQuery = "SELECT drivers.id, drivers.name, "
                + "drivers.license_number "
                + "FROM drivers JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ?;";
        List<Driver> driverList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getDriversStatement = connection.prepareStatement(getDriverQuery)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                driverList.add(getDriver(resultSet));
            }
            return driverList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of drivers "
                    +  "from drivers table. ", throwable);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getObject("id", Long.class);
        String driverName = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(driverName, licenseNumber);
        driver.setId(driverId);
        return driver;
    }

    private void setDrivers(List<Car> cars) {
        if (cars != null) {
            for (Car car : cars) {
                car.setDrivers(getDriverByCar(car.getId()));
            }
        }
    }

    private void deleteDrivers(Car car) {
        String deleteDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteDriversStatement =
                     connection.prepareStatement(deleteDriversQuery)) {
            deleteDriversStatement.setLong(1, car.getId());
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete drivers "
                    +  "from drivers table. ", throwable);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement insertDriversStatement = connection
                     .prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't add drivers to drivers table",
                    throwable);
        }
    }
}
