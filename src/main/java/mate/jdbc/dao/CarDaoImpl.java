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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement createCarStatement = connection
                            .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
                insertDrivers(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get from cars table car: " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id AS id, model, manufacturer_id, "
                + "manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE AND cars.id = ?;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet generatedKeys = getCarStatement.executeQuery();
            if (generatedKeys.next()) {
                car = parseCarWithManufacturerFromResultSet(generatedKeys);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get car from DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, model, manufacturer_id, "
                + "manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet generatedKeys = getAllCarsStatement.executeQuery();
            while (generatedKeys.next()) {
                carList.add(parseCarWithManufacturerFromResultSet(generatedKeys));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all cars from DB", e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        int editedLines;
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE is_deleted = FALSE AND id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            editedLines = updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot update car: " + car, e);
        }
        if (editedLines > 0) {
            deleteDrivers(car.getId());
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        int editedLines;
        String query = "UPDATE cars SET is_deleted = TRUE "
                + "WHERE is_deleted = FALSE AND id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, id);
            editedLines = updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete car from DB by id: " + id, e);
        }
        if (editedLines > 0) {
            deleteDrivers(id);
        }
        return editedLines > 0;
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id AS id, model, manufacturer_id, "
                + "manufacturers.name AS manufacturer_name, "
                + "manufacturers.country AS manufacturer_country "
                + "FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars.is_deleted = FALSE AND cars_drivers.driver_id = ?";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement = connection
                        .prepareStatement(query)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet generatedKeys = getAllCarsByDriverStatement.executeQuery();
            while (generatedKeys.next()) {
                carList.add(parseCarWithManufacturerFromResultSet(generatedKeys));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot get all cars from DB by driver id: "
                    + driverId, e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversForCar(car));
        }
        return carList;
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot insert to cars_drivers drivers: "
                    + car.getDrivers(), e);
        }
    }

    private void deleteDrivers(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversStatement = connection
                        .prepareStatement(query)) {
            deleteDriversStatement.setLong(1, id);
            deleteDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot delete drivers from cars_drivers by id: "
                    + id, e);
        }
    }

    private List<Driver> getDriversForCar(Car car) {
        String query = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ?;";
        List<Driver> driverList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement = connection
                        .prepareStatement(query)) {
            getDriversForCarStatement.setLong(1, car.getId());
            ResultSet generatedKeys = getDriversForCarStatement.executeQuery();
            while (generatedKeys.next()) {
                Driver driver = new Driver();
                driver.setId(generatedKeys.getObject("id", Long.class));
                driver.setName(generatedKeys.getString("name"));
                driver.setLicenseNumber(generatedKeys.getString("license_number"));
                driverList.add(driver);
            }

            return driverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Cannot insert to cars_drivers drivers: "
                    + car.getDrivers(), e);
        }
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet generatedKeys) throws SQLException {
        Car car = new Car();
        car.setId(generatedKeys.getObject("id", Long.class));
        car.setModel(generatedKeys.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(generatedKeys.getObject("manufacturer_id", Long.class));
        manufacturer.setName(generatedKeys.getString("manufacturer_name"));
        manufacturer.setCountry(generatedKeys.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
