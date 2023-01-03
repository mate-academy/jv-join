package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
        String query = "INSERT INTO taxi_service.cars (`model`,`manufacturer_id`) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                           query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "model");
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT * \n"
                + "FROM taxi_service.cars c\n"
                + "JOIN manufacturers m\n"
                + "ON c.manufacturer_id = m.id\n"
                + "WHERE c.id = ? AND c.is_deleted = FALSE ";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getCarQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car with id" + id, e);
        }
        if (car != null) {
            try {
                car.setDrivers(getDriversForCar(id));
            } catch (SQLException e) {
                throw new DataProcessingException("Can't get drivers for car", e);
            }
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        Car car = null;
        String carQuery = "SELECT * \n"
                + "FROM taxi_service.cars c\n"
                + "JOIN manufacturers m\n"
                + "ON c.manufacturer_id = m.id\n"
                + "WHERE c.is_deleted = FALSE ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(carQuery)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all car from DB", e);
        }
        for (Car car1 : cars) {
            try {
                car1.setDrivers(getDriversForCar(car1.getId()));
            } catch (SQLException e) {
                throw new DataProcessingException("Can't get drivers for car", e);
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model= ?,"
                + "manufacturer_id = ?"
                + " WHERE cars.id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car " + car, e);
        }
        deleteDriversFromCar(car);
        addDriversToCar(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        List<Long> carsId = new ArrayList<>();
        String query = "SELECT * \n"
                + "FROM taxi_service.cars c\n"
                + "JOIN cars_drivers cd\n"
                + "ON\t c.id = cd.car_id\n"
                + "WHERE cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsId.add(resultSet.getLong("car_id"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars for driver " + driverId, e);
        }
        for (Long car1 : carsId) {
            cars.add(get(car1).orElseThrow(() -> new NoSuchElementException("Could not get car "
                    + "by id = " + car1)));
        }
        return cars;
    }

    private List<Driver> getDriversForCar(Long carId) throws SQLException {
        List<Driver> drivers = new ArrayList<>();
        String driverQuery = "SELECT * \n"
                + "FROM taxi_service.drivers d\n"
                + "JOIN cars_drivers cd\n"
                + "ON\t d.id = cd.driver_id\n"
                + "WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(driverQuery)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Driver driver = new Driver(
                        resultSet.getObject("id", Long.class),
                        resultSet.getString("name"),
                        resultSet.getString("license_number")
                );
                drivers.add(driver);
            }
        }
        return drivers;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer(
                resultSet.getObject("id", Long.class),
                resultSet.getString("name"),
                resultSet.getString("country")
        );
        car.setId(id);
        car.setModel(model);
        car.setManufacturer(manufacturer);
        return car;
    }

    private void deleteDriversFromCar(Car car) {
        String deleteQuery = "DELETE FROM taxi_service.cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setLong(1,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete driver from car " + car,e);
        }
    }

    private void addDriversToCar(Car car) {
        String insertQuery = "INSERT INTO  `taxi_service`"
                + ".`cars_drivers`(car_id,driver_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setLong(1,car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2,driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver to car " + car,e);
        }
    }
}
