package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        String insertRequest = "INSERT INTO cars(name, manufacturer_id) VALUES (?, ?);";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(insertRequest,
                                Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getName());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
            insertDrivers(car);
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert car to DB. "
                    + car, throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id as car_id, name, "
                + "m.manufacturer_id as manufacturer_id, "
                + "manufacturer_name, manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.manufacturer_id "
                + "WHERE c.id = ?;";
        Car car = null;
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't read car in DB by id. "
                    + id, throwable);
        }
        if (car != null) {
            car.setDriverList(getDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET name = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateRequest)) {
            updateCarStatement.setString(1, car.getName());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("", throwable);
        }
        if (deleteCarFromCarsDriversTable(car)) {
            List<Driver> driverList = car.getDriverList();
            if (driverList != null) {
                for (Driver driver : driverList) {
                    createInCarsDriversTable(car, driver);
                }
            }
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String selectRequest = "SELECT c.id as car_id, name, "
                + "manufacturer_name, m.manufacturer_id as manufacturer_id, "
                + "manufacturer_country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.manufacturer_id "
                + "WHERE c.deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                Statement selectCarsStatement = connection.createStatement()) {
            ResultSet resultSet = selectCarsStatement.executeQuery(selectRequest);
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars from `cars` table", throwable);
        }
        setDriversToCarList(carList);
        return carList;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET deleted = TRUE WHERE id = ?;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car by id. "
                    + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String selectRequest = "SELECT * "
                + "FROM cars_drivers cd "
                + "JOIN cars c "
                + "ON cd.car_id = c.id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.manufacturer_id "
                + "WHERE driver_id = ? AND c.deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection.prepareStatement(selectRequest)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get cars from DB by driver id. "
                    + driverId, throwable);
        }
        setDriversToCarList(carList);
        return carList;
    }

    private boolean deleteCarFromCarsDriversTable(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, car.getId());
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car from cars_drivers table. "
                    + car, throwable);
        }
    }

    private void createInCarsDriversTable(Car car, Driver driver) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarAndDriverStatement
                        = connection.prepareStatement(insertRequest)) {
            insertCarAndDriverStatement.setLong(1, car.getId());
            insertCarAndDriverStatement.setLong(2, driver.getId());
            insertCarAndDriverStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert car, "
                    + car + " and driver, " + driver
                    + " to cars_drivers table", throwable);
        }
    }

    private void insertDrivers(Car car) {
        String insertRequest = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?)";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement
                        = connection.prepareStatement(insertRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't insert drivers to car with id "
                    + car.getId(), throwable);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setName(resultSet.getString("name"));
        car.setId(resultSet.getObject("car_id", Long.class));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString("driver_name"));
        driver.setLicenseNumber(resultSet.getString("driver_license_number"));
        return driver;
    }

    private void setDriversToCarList(List<Car> carList) {
        for (Car car : carList) {
            car.setDriverList(getDrivers(car.getId()));
        }
    }

    private List<Driver> getDrivers(Long id) {
        String getDriversRequest = "SELECT d.driver_id, driver_name, driver_license_number "
                + "FROM drivers d "
                + "JOIN cars_drivers cd "
                + "ON d.driver_id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (
                Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversStatement
                        = connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(parseDriver(resultSet));
            }
            return driverList;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get drivers by id. "
                    + id, throwable);
        }
    }
}
