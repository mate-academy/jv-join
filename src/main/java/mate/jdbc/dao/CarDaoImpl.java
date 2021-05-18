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
        String insertCarQuery =
                   "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(insertCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create " + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long carId) {
        Car car = null;
        String selectCarQuery = "SELECT c.id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = "
                + "FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectCarQuery)) {
            getCarStatement.setLong(1, carId);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }

        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + carId, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriverList(car.getId()));
        }
        return Optional.ofNullable(car);

    }

    @Override
    public List<Car> getAll() {
        String selectAllQuery = "SELECT c.id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id"
                + " AND c.is_deleted = "
                + "FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(selectAllQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.",
                    throwable);
        }
        setDrivers(carList);
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update " + car + " in driversDB.",
                    throwable);
        }
        removeDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection
                        .prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT car_id as id, c.model, m.country, m.name "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id  "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted = FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllByDriverQuery)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.",
                    throwable);
        }
        setDrivers(carList);
        return carList;
    }

    private void removeDrivers(Car car) {
        String deleteDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(deleteDriversQuery)) {
            updateCarStatement.setLong(1, car.getId());
        } catch (SQLException throwable) {
            throw new DataProcessingException(
                    "Couldn't delete drivers associated with car ID: " + car.getId()
                            + " in driversDB.", throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Car car = new Car();
        car.setModel(model);
        car.setManufacturer(getManufacturer(resultSet));
        car.setId(carId);
        return car;
    }

    private List<Driver> getDriverList(Long carId) {
        String query = "SELECT cd.car_id as id, name, license_number FROM drivers d JOIN "
                + "cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.is_deleted = "
                + "FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverListStatement = connection.prepareStatement(query)) {
            getDriverListStatement.setLong(1, carId);
            ResultSet resultSet = getDriverListStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(getDriver(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get a list of drivers from DB with ID: "
                            + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(newId);
        return manufacturer;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement = connection
                        .prepareStatement(insertDriversQuery)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car ID: " + car.getId(), e);
        }
    }

    private void setDrivers(List<Car> carList) {
        if (carList != null) {
            for (Car car : carList) {
                car.setDrivers(getDriverList(car.getId()));
            }
        } else {
            throw new RuntimeException("Can't assign driver to empty list");
        }
    }
}
