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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertCarRequest =
                "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(
                                 insertCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert car to DB " + insertCarRequest, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarByIdRequest =
                "SELECT cars.id, cars.model, cars.manufacturer_id, manufacturers.name, "
                        + "manufacturers.country FROM cars INNER JOIN manufacturers "
                        + "ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByIdStatement =
                        connection.prepareStatement(getCarByIdRequest)) {
            getCarsByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarsByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find car in DB by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(car));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest =
                "SELECT * FROM cars JOIN manufacturers "
                        + "ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement =
                        connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars from DB " + cars, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest =
                "UPDATE cars SET model = ?, manufacturer_id = ? "
                        + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update car to DB " + car, e);
        }
        deleteDriversFromCar(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest =
                "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement softDeleteCarStatement =
                         connection.prepareStatement(deleteCarRequest)) {
            softDeleteCarStatement.setLong(1, id);
            return softDeleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllRequest =
                "SELECT * FROM cars INNER JOIN manufacturers "
                        + "ON cars.manufacturer_id = manufacturers.id INNER "
                        + "JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                        + "WHERE cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement =
                        connection.prepareStatement(getAllRequest)) {
            getAllStatement.setLong(1, driverId);
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get car by driver id " + driverId, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversForCar(car));
        }
        return cars;
    }

    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id", Long.class));
        car.setModel(resultSet.getString("model"));
        Long idManufacturer = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer =
                new Manufacturer(idManufacturer, manufacturerName, manufacturerCountry);
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Car car) {
        List<Driver> drivers = new ArrayList<>();
        String getAllDriversForCarRequest =
                "SELECT * FROM drivers AS d INNER JOIN cars_drivers AS cd "
                        + "ON cd.driver_id = d.id WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversForCarStatement =
                         connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversForCarStatement.setLong(1, car.getId());
            ResultSet resultSet = getAllDriversForCarStatement.executeQuery();
            while (resultSet.next()) {
                Long idDriver = resultSet.getObject("driver_id", Long.class);
                String name = resultSet.getString("name");
                String licenseNumber = resultSet.getString("license_number");
                drivers.add(new Driver(idDriver, name, licenseNumber));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers of car " + car, e);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest =
                "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement addDriverToCarStatement =
                         connection.prepareStatement(insertDriversRequest)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers for car: " + car, e);
        }
    }

    private void deleteDriversFromCar(Car car) {
        String deleteDriverRequest =
                "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteDriverFromCarStatement =
                         connection.prepareStatement(deleteDriverRequest)) {
            deleteDriverFromCarStatement.setLong(1, car.getId());
            deleteDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete drivers from car " + car, e);
        }
    }
}
