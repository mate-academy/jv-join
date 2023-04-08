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
    public Car create(Car car) { // Fixed
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create a car "
                    + car + ". ", e);
        }
        if (car.getDrivers() != null) {
            insertCarDrivers(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarByIdQuery = "SELECT c.id AS car_id, c.model AS car_model, "
                + "c.manufacturer_id AS car_manufacturer_id, "
                + "m.id AS manufacturer_id , m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars AS c "
                + "JOIN manufacturers AS m "
                + "ON c.manufacturer_id = manufacturer_id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement
                        = connection.prepareStatement(getCarByIdQuery)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT c.id AS car_id, c.model AS car_model, "
                + "c.manufacturer_id AS car_manufacturer_id, "
                + "m.id AS manufacturer_id , m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB.", e);
        }
        for (Car car : cars) {
            car.setDrivers(getCarDrivers(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer()
                    .getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
        deleteDriversFromCar(car);
        insertCarDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllCarsByDriverQuery = "SELECT c.id AS car_id, c.model AS car_model, "
                + "c.manufacturer_id AS car_manufacturer_id, "
                + "m.id AS manufacturer_id , m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country "
                + "FROM cars AS c "
                + "JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd "
                + "ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriver
                        = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsByDriver.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriver.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars "
                    + "by driver id " + driverId, e);
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet
                .getObject(3, Long.class);
        String manufacturerName = resultSet
                .getString(4);
        String manufacturerCountry = resultSet
                .getString(5);
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerName,
                manufacturerCountry);

        Long id = resultSet.getObject(1, Long.class);
        String model = resultSet.getString(2);
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setManufacturer(manufacturer);
        return car;
    }

    private void insertCarDrivers(Car car) { // Fixed
        if (car.getDrivers() != null && !car.getDrivers().isEmpty()) {
            String insertDriversToCarQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                    + "VALUES (?, ?);";
            try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement insertDriversToCarStatement = connection
                             .prepareStatement(insertDriversToCarQuery)) {
                insertDriversToCarStatement.setLong(1, car.getId());
                for (Driver driver : car.getDrivers()) {
                    insertDriversToCarStatement.setLong(2, driver.getId());
                    insertDriversToCarStatement.executeUpdate();

                }
            } catch (SQLException e) {
                throw new DataProcessingException("Can't insert drivers to car "
                        + car + ". ", e);
            }
        }
    }

    private List<Driver> getCarDrivers(Long carId) {
        List<Driver> carDrivers = new ArrayList<>();
        String getCarDriversQuery = "SELECT d.id, d.name, d.license_number "
                + "FROM drivers AS d "
                + "JOIN cars_drivers AS cd "
                + "ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ? "
                + "AND d.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarDriversStatement = connection
                        .prepareStatement(getCarDriversQuery)) {
            getCarDriversStatement.setLong(1, carId);
            ResultSet resultSet = getCarDriversStatement.executeQuery();
            Driver driver;

            while (resultSet.next()) { // Should I create a separate method getDriver()?
                //getDriver(resultSet);
                driver = new Driver();
                driver.setId(resultSet.getLong(3));
                driver.setName(resultSet.getString(4));
                driver.setLicenseNumber(resultSet.getString(5));
                carDrivers.add(driver);
            }
            return carDrivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car drivers list. Car id: "
                    + carId + ". ", e);
        }
    }

    private void deleteDriversFromCar(Car car) {
        String deleteDriversFromCarQuery = "DELETE "
                + "FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversFromCarStatement
                        = connection.prepareStatement(deleteDriversFromCarQuery)) {
            deleteDriversFromCarStatement.setLong(1, car.getId());
            deleteDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from car "
                    + car, e);
        }
    }
}
