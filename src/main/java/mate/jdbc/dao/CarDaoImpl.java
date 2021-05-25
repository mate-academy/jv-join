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
                "INSERT INTO cars (car_model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement = connection
                        .prepareStatement(insertCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getCarModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarQuery = "SELECT c.id, c.car_model, m.name, m.country FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id WHERE c.id = ? AND c.deleted = "
                + "FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(getCarQuery)) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriverList(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT c.id, c.car_model, m.name, m.country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id AND c.deleted = "
                + "FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.", e);
        }
        setDrivers(carList);
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET car_model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection
                        .prepareStatement(updateCarQuery)) {
            updateStatement.setString(1, car.getCarModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            if (updateStatement.executeUpdate() == 0) {
                throw new RuntimeException(
                        "No such car to update: " + car.getId());
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update " + car + " ", e);
        }
        removeDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection
                        .prepareStatement(deleteCarQuery)) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT car_id as id, c.car_model, m.country, m.name "
                + "FROM cars_drivers cd JOIN cars c ON c.id = cd.car_id  "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.deleted = FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllByDriverQuery)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCar(resultSet);
                carList.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from driversDB.", e);
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
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't delete drivers from car with id: " + car.getId()+" ", e);
        }
    }

    private List<Driver> getDriverList(Long carId) {
        String getDriverListQuery = "SELECT cd.car_id as id, name, licenseNumber FROM drivers d JOIN "
                + "cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? AND d.deleted = "
                + "FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverListStatement = connection.prepareStatement(getDriverListQuery)) {
            getDriverListStatement.setLong(1, carId);
            ResultSet resultSet = getDriverListStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(getDriver(resultSet));
            }
            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Couldn't get a list of drivers car ID: " + carId + " ", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setCarModel(resultSet.getString("car_model"));
        car.setManufacturer(getManufacturer(resultSet));
        car.setId(resultSet.getObject("id", Long.class));
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("name"),
                resultSet.getString("licenseNumber"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer(resultSet.getString("name"),
                resultSet.getString("country"));
        manufacturer.setId(resultSet.getObject("id", Long.class));
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
            throw new DataProcessingException("Can't add drivers to car with ID: "
                    + car.getId(), e);
        }
    }

    private void setDrivers(List<Car> carList) {
        if (carList != null) {
            carList.forEach(car -> car.setDrivers(getDriverList(car.getId())));
        } else {
            throw new RuntimeException("Can't assign driver to empty list");
        }
    }
}
