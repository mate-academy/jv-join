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
        String insertQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create a car "
                    + car + ". ", e);
        }
        if (car.getDrivers() != null) {
            insertCarDrivers(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarByIdQuery = "SELECT cars.id, cars.model, cars.manufacturer_id, "
                + "manufacturers.id, manufacturers.name, manufacturers.country "
                + "FROM cars "
                + "JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarByIdStatement
                        = connection.prepareStatement(getCarByIdQuery)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getCarDrivers(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT * FROM cars WHERE is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection
                        .prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getLong(1)).get());
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers from driversDB.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(query)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer()
                    .getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car "
                    + car, e);
        }
        deleteDriversFromCar(car);
        insertCarDrivers(car);
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
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getAllCarsByDriverQuery = "SELECT * "
                + "FROM cars "
                + "JOIN cars_drivers "
                + "ON cars.id = cars_drivers.car_id "
                + "WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriver
                        = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsByDriver.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriver.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet
                        .getLong(1))
                        .get());
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + driverId, e);
        }
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet
                .getObject("manufacturers.id", Long.class);
        String manufacturerName = resultSet
                .getString("manufacturers.name");
        String manufacturerCountry = resultSet
                .getString("manufacturers.country");
        Manufacturer manufacturer = new Manufacturer(manufacturerId,
                manufacturerName,
                manufacturerCountry);

        Long id = resultSet.getObject("cars.id", Long.class);
        String model = resultSet.getString("cars.model");
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        car.setManufacturer(manufacturer);
        return car;
    }

    private void insertCarDrivers(Car car) {
        String insertDriversToCarQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversToCarStatement = connection
                        .prepareStatement(insertDriversToCarQuery)) {
            insertDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversToCarStatement.setLong(2, driver.getId());
                insertDriversToCarStatement.executeUpdate();

            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car "
                    + car + ". ", e);
        }
    }

    private List<Driver> getCarDrivers(Long carId) {
        List<Driver> carDrivers = new ArrayList<>();
        String getCarDriversQuery = "SELECT * "
                + "FROM cars_drivers "
                + "JOIN drivers "
                + "ON cars_drivers.driver_id = drivers.id "
                + "WHERE cars_drivers.car_id = ? "
                + "AND drivers.is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarDriversStatement = connection
                        .prepareStatement(getCarDriversQuery)) {
            getCarDriversStatement.setLong(1, carId);
            ResultSet resultSet = getCarDriversStatement.executeQuery();
            Driver driver;

            while (resultSet.next()) {
                driver = new Driver();
                driver.setId(resultSet.getLong(3));
                driver.setName(resultSet.getString(4));
                driver.setLicenseNumber(resultSet.getString(5));
                carDrivers.add(driver);
            }
            return carDrivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car drivers list. Car id: "
                    + carId + ". ", e);
        }
    }

    private void deleteDriversFromCar(Car car) {
        String deleteDriversFromCarQuery = "DELETE "
                + "FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteDriversFromCarStatement
                        = connection.prepareStatement(deleteDriversFromCarQuery)) {
            deleteDriversFromCarStatement.setLong(1, car.getId());
            deleteDriversFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers from car "
                    + car, e);
        }
    }
}
