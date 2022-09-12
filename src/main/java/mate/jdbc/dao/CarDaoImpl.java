package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertQuery = "INSERT INTO cars (manufacturer_id, model)"
                + " VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't create car.", e);
        }
        insertDriverFromCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id, model, manufacturers.id, name, country"
                + " FROM cars"
                + " INNER JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get a car by id= " + id, e);
        }
        if (car != null) {
            car.setDrivers(parseDriversByCarId(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, model, manufacturers.id, name, country"
                + " FROM cars"
                + " LEFT JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = parseCarWithManufacturer(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars.", e);
        }
        for (Car car : cars) {
            car.setDrivers(parseDriversByCarId(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?,"
                + " manufacturer_id = ?"
                + " WHERE id = ? AND cars.is_deleted = FALSE;";
        boolean updated;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        updateQuery)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.setObject(3, car.getId());
            updated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car by id=" + car.getId(), e);
        }
        if (updated) {
            deleteRalationsipCarsDrivers(car);
            insertDriverFromCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE"
                + " WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car by id= " + id, e);
        }
    }

    private List<Driver> parseDriversByCarId(Long id) {
        String query = "SELECT id, name, license_number"
                + " FROM drivers"
                + " INNER JOIN cars_drivers AS ca"
                + " ON drivers.id = ca.driver_id"
                + " WHERE ca.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> driversList = new ArrayList<>();
            while (resultSet.next()) {
                driversList.add(parseDriversFromResultSet(resultSet));
            }
            return driversList;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers by car id=" + id, e);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Long driverId = resultSet.getLong(1);
        String name = resultSet.getString(2);
        String license = resultSet.getString(3);
        return new Driver(driverId, name, license);
    }

    private Car parseCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long carId = resultSet.getLong(1);
        String carModel = resultSet.getString(2);
        Long manufacturerId = resultSet.getLong(3);
        String manufacturerName = resultSet.getString(4);
        String manufacturerCountry = resultSet.getString(5);
        return new Car(carId, carModel,
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry));
    }

    private void insertDriverFromCar(Car car) {
        String insertQuery = "INSERT INTO cars_drivers (car_id, driver_id)"
                + " VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert driver by car id= " + car.getId(), e);
        }
    }

    private void deleteRalationsipCarsDrivers(Car car) {
        String deleteDrivers = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(deleteDrivers)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update drivers by car id= " + car.getId(), e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String querySelectCarsByDriver = "SELECT cars.id"
                + " FROM cars"
                + " INNER JOIN cars_drivers"
                + " ON cars.id = cars_drivers.car_id"
                + " WHERE cars_drivers.driver_id = ? AND cars.is_deleted = FALSE;";
        List<Long> carsId = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(querySelectCarsByDriver)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carsId.add(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get a car by driver id= " + driverId, e);
        }
        List<Car> carList = new ArrayList<>();
        for (Long carId : carsId) {
            carList.add(get(carId).get());
        }
        return carList;
    }
}
