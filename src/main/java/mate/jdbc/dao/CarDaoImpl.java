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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't create car " + car, throwables);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT model, manufacturer_id FROM cars WHERE id = ? "
                + "AND is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
                car.setId(id);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car from dataBase by id - "
                    + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversFromCurrentCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * FROM cars WHERE is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                Long carsId = resultSet.getObject("id", Long.class);
                Car car = parseCar(resultSet);
                car.setId(carsId);
                car.setDrivers(getDriversFromCurrentCar(carsId));
                cars.add(car);
            }
            return cars;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get all cars", throwables);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?,"
                   + "manufacturer_id = ? WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't update current car - " + car,
                    throwables);
        }
        if (car != null) {
            deleteDriversRelationsWithCar(car);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete car by id " + id,
                    throwables);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT id AS cars_id, model AS cars_model, manufacturer_id FROM cars c"
                + " JOIN cars_drivers cd ON c.id = cd.cars_id"
                + " WHERE cd.drivers_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            List<Car> cars = new ArrayList<>();
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getObject("cars_id", Long.class))
                             .orElseThrow(() ->
                         new DataProcessingException("Can't get car from dataBase by driver's id - "
                             + driverId)));
            }
            return cars;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get cars by drivers id " + driverId,
                    throwables);
        }
    }

    private List<Driver> getDriversFromCurrentCar(Long id) {
        String query = "SELECT id as id_of_driver, name as driver_name, license_number "
                + "FROM cars_drivers JOIN drivers ON cars_drivers.drivers_id = drivers.id"
                + " WHERE cars_drivers.cars_id = ? and drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get drivers from car with current id "
                    + id, throwables);
        }
    }

    private void deleteDriversRelationsWithCar(Car car) {
        String query = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete relations with current car "
                    + car + " from cars_drivers table", throwables);
        }
    }

    private void insertDrivers(Car car) {
        String query = "INSERT INTO cars_drivers(cars_id, drivers_id) VALUES(?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(1, car.getId());
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert drivers into car "
                    + car + "in cars_drivers table", throwables);
        }
    }

    private Driver getDriver(ResultSet resultSet) {
        try {
            Long id = resultSet.getObject("id_of_driver", Long.class);
            String name = resultSet.getString("driver_name");
            String licenseNumber = resultSet.getString("license_Number");
            Driver driver = new Driver(name, licenseNumber);
            driver.setId(id);
            return driver;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get driver by input data", throwables);
        }
    }

    private Car parseCar(ResultSet resultSet) {
        try {
            Long manufacturerId = resultSet.getLong("manufacturer_id");
            Manufacturer manufacturer = getManufacturerById(manufacturerId);
            return new Car(manufacturer, resultSet.getString("model"));
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get car by input data"
                    + "in parseCar() method", throwables);
        }
    }

    private Manufacturer getManufacturerById(Long id) {
        String query = "SELECT id, name, country FROM manufacturers "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Manufacturer manufacturer = null;
            if (resultSet.next()) {
                manufacturer = getManufacturer(resultSet);
            }
            return manufacturer;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get manufacturer by car's id " + id,
                    throwables);
        }
    }

    private Manufacturer getManufacturer(ResultSet resultSet) {
        try {
            String name = resultSet.getString("name");
            String country = resultSet.getString("country");
            Long id = resultSet.getObject("id", Long.class);
            Manufacturer manufacturer = new Manufacturer(name, country);
            manufacturer.setId(id);
            return manufacturer;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get manufacturer by input data",
                    throwables);
        }
    }
}
