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
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't create car with id = " + car.getId(),
                    throwables);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectRequest = "SELECT c.id as car_id, c.model, m.id as manufacturer_id, "
                + "m.name, m.country\n"
                + "FROM cars c  JOIN manufacturers m ON c.id = m.id WHERE c.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }

        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car with id = " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT * FROM cars WHERE is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = getCarsFromResultSet(resultSet);
            return cars;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get cars list from cardDB ", throwables);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?"
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        deleteRelationBetweenCarAanDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? AND is_deleted = false";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriverQuery = "SELECT c.id, c.model "
                + "FROM cars c JOIN cars_drivers cd ON c.id = car_id WHERE cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriversStatement =
                        connection.prepareStatement(getCarsByDriverQuery)) {
            getAllByDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriversStatement.executeQuery();
            List<Car> cars = getCarsFromResultSet(resultSet);
            return cars;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get cars by driver ", throwables);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDrivers = "Select  d.id, d.name, d.license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCar = connection.prepareStatement(getDrivers)) {
            getDriversForCar.setLong(1, id);
            ResultSet resultSet = getDriversForCar.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get drivers for car with id = ");
        }
    }

    private void deleteRelationBetweenCarAanDrivers(Car car) {
        String deleteRelationsQuery = "DELETE  FROM cars_drivers "
                + "WHERE car_id = ? AND driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationStatement = connection
                        .prepareStatement(deleteRelationsQuery)) {
            deleteRelationStatement.setLong(1, car.getId());
            List<Driver> deleteRelationsWithDrivers;
            deleteRelationsWithDrivers = getDriversForCar(car.getId());
            for (Driver d : deleteRelationsWithDrivers) {
                deleteRelationStatement.setLong(2, d.getId());
                deleteRelationStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete relationship between drivers and car "
                    + car, throwables);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCar = connection
                        .prepareStatement(insertDriversQuery)) {
            addDriversToCar.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCar.setLong(2, driver.getId());
                addDriversToCar.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, throwables);
        }
    }

    private List<Car> getCarsFromResultSet(ResultSet resultSet) throws SQLException {
        List<Car> cars = new ArrayList<>();
        while (resultSet.next()) {
            Car car = new Car();
            car.setId(resultSet.getObject("id", Long.class));
            car.setModel(resultSet.getString("model"));
            cars.add(car);
        }
        return cars;
    }
}
