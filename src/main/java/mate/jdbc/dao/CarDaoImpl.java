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
                PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setLong(2, car.getManufacturer().getId());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't create car "
                   + car + ". ", ex);
        }
        addAllDriversByCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, m.name, m.county "
                + "FROM cars c LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithoutDrivers(resultSet);
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't get car by id " + id, ex);
        }
        if (car != null) {
            car.setDrivers(getAllDriversByCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id AS car_id, c.model, m.id AS manufacturer_id, m.name, "
                + "m.country FROM cars c "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> listCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarStatement
                         = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                listCars.add(getCarWithoutDrivers(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't get all cars", ex);
        }
        for (Car car : listCars) {
            car.setDrivers(getAllDriversByCar(car));
        }
        return listCars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement updateCarStatement
                           = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", ex);
        }
        if (car.getDrivers() != null) {
            hardDeleteAllDriversByCar(car.getId());
            addAllDriversByCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        int operationCount;
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement updateCarStatement
                            = connection.prepareStatement(query)) {
            updateCarStatement.setLong(1, id);
            operationCount = updateCarStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't delete car with id: "
                    + id, ex);
        }
        if (operationCount == 0) {
            return false;
        }
        hardDeleteAllDriversByCar(id);
        return true;
    }

    @Override
    public List<Car> getAllCarsByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model, c.manufacturer_id as manufacturer_id, "
                + "m.name, m.country, cd.driver_id AS driver_id "
                + "FROM cars c "
                + "LEFT JOIN manufacturers m ON manufacturer_id = m.id "
                + "LEFT JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE driver_id = '2' AND c.is_deleted = FALSE;";
        List<Car> listCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement
                        = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                listCars.add(getCarWithoutDrivers(resultSet));
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't get all cars by driver id: "
                    + driverId, ex);
        }
        for (Car car : listCars) {
            car.setDrivers(getAllDriversByCar(car));
        }
        return listCars;
    }

    private Car getCarWithoutDrivers(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(manufacturerId);
        return new Car(carId, model, manufacturer);
    }

    private void addAllDriversByCar(Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addAllDriversByCarStatement
                        = connection.prepareStatement(query)) {
            List<Driver> listDrivers = car.getDrivers();
            addAllDriversByCarStatement.setLong(2, car.getId());
            for (Driver driver : listDrivers) {
                addAllDriversByCarStatement.setLong(1, driver.getId());
                addAllDriversByCarStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't add all drivers from car: " + car, ex);
        }
    }

    private List<Driver> getAllDriversByCar(Car car) {
        String query = "SELECT d.* "
                + "FROM cars_drivers cd "
                + "LEFT JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversByCarStatement
                        = connection.prepareStatement(query)) {
            List<Driver> listDrivers = new ArrayList<>();
            getAllDriversByCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getAllDriversByCarStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                listDrivers.add(driver);
            }
            return listDrivers;
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't add all drivers from car: " + car, ex);
        }
    }

    private void hardDeleteAllDriversByCar(Long id) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement hardDeleteAllDriversByCarIdStatement
                        = connection.prepareStatement(query)) {
            hardDeleteAllDriversByCarIdStatement.setLong(1, id);
            hardDeleteAllDriversByCarIdStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataProcessingException("Couldn't delete all drivers from car with id: "
                    + id, ex);
        }
    }
}
