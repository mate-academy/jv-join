package mate.jdbc.dao;

import java.math.BigDecimal;
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
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, color, price) "
                + "VALUES (?, ?, ?) ";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement saveCarStatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            saveCarStatement.setString(1, car.getModel());
            saveCarStatement.setString(2, car.getColor());
            saveCarStatement.setBigDecimal(3, car.getPrice());
            saveCarStatement.executeUpdate();
            ResultSet resultSet = saveCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT "
                + "    id as car_id, model, color, price "
                + "FROM "
                + "    cars "
                + "WHERE "
                + "    id = ? and is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(getDriversByCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT "
                + "    id as car_id, model, color, price "
                + "FROM "
                + "    cars "
                + "WHERE "
                + "    is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCar(car));
        }
        return cars;
    }

    @Override
    public List<Car> getAllByDriverId(Long driverId) {
        String query = "SELECT "
                + "    id as car_id, model, color, price "
                + "FROM "
                + "    cars c "
                + "        JOIN "
                + "    cars_drivers cd ON c.id = cd.car_id "
                + "WHERE "
                + "    driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatementByDriverId
                         = connection.prepareStatement(query)) {
            getAllCarsStatementByDriverId.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatementByDriverId.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB byDriverId.",
                    throwable);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCar(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, color = ?,"
                + "price = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                            = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setString(2, car.getColor());
            updateCarStatement.setBigDecimal(3, car.getPrice());
            updateCarStatement.setLong(4, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in carsDB.", throwable);
        }
        updatedCarDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement
                            = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a car by id " + id + " ",
                    throwable);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        String color = resultSet.getString("color");
        BigDecimal price = resultSet.getBigDecimal("price");
        Car car = new Car(model, color);
        car.setId(carId);
        car.setPrice(price);
        return car;
    }

    private List<Driver> getDriversByCar(Car car) {
        String query = "SELECT "
                + "    id, name, license_number "
                + "FROM "
                + "    drivers d "
                + "        JOIN "
                + "    cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE "
                + "    car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversStatementByCarId
                         = connection.prepareStatement(query)) {
            getDriversStatementByCarId.setLong(1, car.getId());
            ResultSet resultSet = getDriversStatementByCarId.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException(
                    "Couldn't get a list of drivers from driversDB by car.",
                    throwable);
        }
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Long newId = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(newId);
        return driver;
    }

    private void updatedCarDrivers(Car car) {
        Long carId = car.getId();
        List<Driver> drivers = car.getDrivers();
        deleteCarDrivers(carId);
        if (drivers.size() > 0) {
            String sourceOfQuery = "insert into cars_drivers(car_id,driver_id) values(";
            try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement updateDriversByCarIdStatement
                                = connection.prepareStatement(sourceOfQuery)) {
                for (Driver driver : drivers) {
                    updateDriversByCarIdStatement.addBatch(sourceOfQuery
                            + carId + ", "
                            + driver.getId()
                            + ");");
                }
                updateDriversByCarIdStatement.executeBatch();
            } catch (SQLException throwable) {
                throw new DataProcessingException("Couldn't update drivers for car "
                        + car.getModel(),
                        throwable);
            }
        }
    }

    private void deleteCarDrivers(Long carId) {
        String query =
                "DELETE FROM cars_drivers "
                        + "WHERE"
                        + " car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteAllByCarIdStatement = connection.prepareStatement(query)) {
            deleteAllByCarIdStatement.setLong(1, carId);
            deleteAllByCarIdStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete drivers by carId.",
                    throwable);
        }
    }

}
