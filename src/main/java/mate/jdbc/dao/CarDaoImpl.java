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
        String insertRequest = "INSERT INTO cars (manufacturer_id, model) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertCarStatement
                         = connection.prepareStatement(insertRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setLong(1, car.getManufacturer().getId());
            insertCarStatement.setString(2, car.getModel());
            insertCarStatement.executeUpdate();
            ResultSet generatedKeys = insertCarStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create " + car + " .", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarByIdQuery
                = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement
                        = connection.prepareStatement(getCarByIdQuery)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id: " + id + " .", e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getCarsQuery
                = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarsStatement = connection.prepareStatement(getCarsQuery)) {
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get cars. ", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCar = "UPDATE cars SET manufacturer_id = ?, model = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement = connection.prepareStatement(updateCar)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car: " + car, e);
        }
        deleteDrivers(car);
        insertDrivers(car);
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
            throw new DataProcessingException("Couldn't delete car by id: " + id + " .", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getCarsByDriverQuery
                = "SELECT c.id AS 'car_id', model, m.id AS 'manufacturer_id', name, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE driver_id = ? AND c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarsByDriverStatement
                         = connection.prepareStatement(getCarsByDriverQuery)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car's by driver id: "
                    + driverId + " .", e);
        }
        cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        return cars;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertCarDrivers
                         = connection.prepareStatement(insertDriversQuery)) {
            insertCarDrivers.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertCarDrivers.setLong(1, driver.getId());
                insertCarDrivers.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert Drivers " + car + " .", e);
        }
    }

    private void deleteDrivers(Car car) {
        String deleteDriversQuery = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarDriversStatement
                        = connection.prepareStatement(deleteDriversQuery)) {
            deleteCarDriversStatement.setLong(1, car.getId());
            deleteCarDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete Drivers " + car + " .", e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversQuerry = "SELECT d.id AS driver_id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE car_id = ?";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversStatement
                         = connection.prepareStatement(getDriversQuerry)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                Long driverId = resultSet.getObject("driver_id", Long.class);
                String driverName = resultSet.getString("name");
                String driverLicenseNumber = resultSet.getString("license_number");
                drivers.add(new Driver(driverId, driverName, driverLicenseNumber));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get Drivers by car id: " + id + " .", e);
        }
        return drivers;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        long manufacturerId = resultSet.getLong("manufacturer_id");
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer
                = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        long carId = resultSet.getLong("car_id");
        String carModel = resultSet.getString("model");
        return new Car(carId, carModel, manufacturer);
    }
}
