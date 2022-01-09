package mate.jdbc.dao;

import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
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
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement createStatement = connection
                     .prepareStatement(createCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet resultSet = createStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }

        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't create new car: " + car, throwables);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT cars.id, model, manufacturer_id, man.name, man.country "
                + "FROM cars "
                + "JOIN manufacturers man "
                + "ON manufacturer_id = man.id "
                + "WHERE cars.is_deleted = false AND cars.id = ?";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarByIdStatement = connection.prepareStatement(getCarQuery)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car by id: " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversByCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getCarsQuery = "SELECT cars.id, model, manufacturer_id, man.name, man.country "
                + "FROM cars "
                + "JOIN manufacturers man "
                + "ON manufacturer_id = man.id "
                + "WHERE cars.is_deleted = false";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarsStatement = connection.prepareStatement(getCarsQuery)) {
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                car.setDrivers(getDriversByCar(car));
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car list", throwables);
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        updateCar(car);
        deleteDriversFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String softDeleteQuery = "UPDATE cars SET is_deleted = true WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteCarByIdStatement = connection
                .prepareStatement(softDeleteQuery)) {
            deleteCarByIdStatement.setLong(1, id);
            return deleteCarByIdStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete car by id: " + id,
                    throwables);
        }
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String addDriverQuery = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement addDriverStatement = connection.prepareStatement(addDriverQuery)) {
            addDriverStatement.setLong(1, driver.getId());
            addDriverStatement.setLong(2, car.getId());
            addDriverStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't add driver: " + driver + " to car: " + car, throwables);
        }

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String removeDriverFromCarQuery = "DELETE FROM cars_drivers "
                + "WHERE driver_id = ? AND car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement deleteDriverFromCarStatement = connection
                .prepareStatement(removeDriverFromCarQuery)) {
            deleteDriverFromCarStatement.setLong(1, driver.getId());
            deleteDriverFromCarStatement.setLong(2, car.getId());
            deleteDriverFromCarStatement.executeUpdate();

        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't remove driver: "
                    + driver + " from car: " + car, throwables);
        }

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverIdQuery = "SELECT c.id , c.model, c.manufacturer_id, m.name, m.country "
                + "FROM cars_drivers cd "
                + "JOIN cars c "
                + "ON cd.car_id = c.id "
                + "JOIN drivers d "
                + "ON cd.driver_id = d.id "
                + "JOIN manufacturers m "
                + "ON m.id = c.manufacturer_id "
                + "WHERE c.is_deleted = false AND d.is_deleted = false AND cd.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarsByDriverIdIdStatement = connection
                     .prepareStatement(getAllCarsByDriverIdQuery)) {
            getCarsByDriverIdIdStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverIdIdStatement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarFromResultSet(resultSet);
                car.setDrivers(getDriversByCar(car));
                cars.add(car);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car list by driver id: "
                    + driverId, throwables);
        }
        return cars;

    }


    private List<Driver> getDriversByCar(Car car) {
        String getDriversByCarQuery = "SELECT d.id, d.name, d.license_number "
                + "FROM cars_drivers cd "
                + "JOIN drivers d "
                + "ON cd.driver_id = d.id "
                + "WHERE d.is_deleted = false AND cd.car_id = ?;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement getDriversByCarStatement = connection
                .prepareStatement(getDriversByCarQuery)) {
            getDriversByCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getDriversByCarStatement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getObject("name", String.class));
                driver.setLicenseNumber(resultSet.getObject("license_number", String.class));
                drivers.add(driver);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get drivers by car: " + car, throwables);
        }
        return drivers;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        car.setId(resultSet.getObject("id", Long.class));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getObject("name", String.class));
        manufacturer.setCountry(resultSet.getObject("country", String.class));
        car.setManufacturer(manufacturer);
        car.setModel(resultSet.getObject("model", String.class));
        return car;
    }

    private void updateCar(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateCarStatement = connection
                     .prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't update car: " + car, throwables);
        }
    }

    private void deleteDriversFromCar(Car car) {
        String deleteAllRelationsByCar = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement removeDriversFromCarStatement = connection
                     .prepareStatement(deleteAllRelationsByCar)) {
            removeDriversFromCarStatement.setLong(1, car.getId());
            removeDriversFromCarStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't remove drivers from car: "
                    + car, throwables);
        }
    }

    private void addDriversToCar(Car car) {
        car.getDrivers().forEach((d) -> addDriverToCar(d, car));
    }
}
