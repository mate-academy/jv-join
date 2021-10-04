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
        String query = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car. " + car, e);
        }
        createRelationCarWithDriver(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT * "
                + "FROM cars "
                + "INNER JOIN manufacturers ON manufacturers.id = manufacturer_id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(query)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.setDrivers(getAllDriverByCar(car.getId()));
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT * "
                + "FROM cars "
                + "JOIN manufacturers ON manufacturers.id = manufacturer_id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement
                        = connection.prepareStatement(query)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table. ", e);
        }
        for (Car car : cars) {
            List<Driver> allDriverByCar = getAllDriverByCar(car.getId());
            car.setDrivers(getAllDriverByCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement
                         = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
        deleteRelationCarWithDriver(car);
        createRelationCarWithDriver(car);
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT *\n"
                + "FROM cars\n"
                + "INNER JOIN cars_drivers ON cars.id = cars_drivers.car_id\n"
                + "INNER JOIN manufacturers ON manufacturers.id = cars.manufacturer_id\n"
                + "WHERE cars_drivers.driver_id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarByDriversStatement
                         = connection.prepareStatement(query)) {
            getAllCarByDriversStatement.setLong(1, driverId);
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = getAllCarByDriversStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table with driverId = " + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("cars.id", Long.class);
        String model = resultSet.getString("cars.model");
        Long manufacturerId = resultSet.getObject("manufacturers.id", Long.class);
        String manufacturerName = resultSet.getString("manufacturers.name");
        String manufacturerCountry = resultSet.getString("manufacturers.country");
        Manufacturer manufacturer =
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        return new Car(id, model, manufacturer);
    }

    private List<Driver> getAllDriverByCar(Long carId) {
        String query = "SELECT *\n"
                + "FROM drivers\n"
                + "INNER JOIN cars_drivers ON drivers.id = cars_drivers.driver_id\n"
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriverByCarsStatement
                         = connection.prepareStatement(query)) {
            getAllDriverByCarsStatement.setLong(1, carId);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = getAllDriverByCarsStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers "
                    + "from drivers table with carId = " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("drivers.id", Long.class);
        String name = resultSet.getString("drivers.name");
        String licenseNumber = resultSet.getString("drivers.license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void createRelationCarWithDriver(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement
                        = connection.prepareStatement(query)) {
            createCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                createCarStatement.setLong(2, driver.getId());
                createCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create cars_drivers relations. " + car, e);
        }
    }

    private void deleteRelationCarWithDriver(Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationStatement
                         = connection.prepareStatement(query)) {
            deleteRelationStatement.setLong(1, car.getId());
            deleteRelationStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete cars_drivers relations. " + car, e);
        }
    }
}
