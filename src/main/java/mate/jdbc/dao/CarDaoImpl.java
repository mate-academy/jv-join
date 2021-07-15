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
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Inject
    private DriverDao driverDao;

    @Override
    public Car create(Car car) {
        String insertQuery = "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            insertDriversToCar(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't creat car to database: " + car, e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarById = "SELECT c.id as car_id, model, name, m.id as manufacturer_id, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getCarById)) {
            statement.setLong(1, id);
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();
            Car car = null;
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
            return Optional.ofNullable(car);
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from database by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAll() {
        String getAllCar = "SELECT c.id as car_id, model, name, m.id as manufacturer_id, country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getAllCar)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from database.", e);
        }
    }

    @Override
    public Car update(Car car) {
        String updateCar = "UPDATE cars "
                + " SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateCar)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
            deleteDriversFromCar(car);
            insertDriversToCar(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car in database: " + car, e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String deleteCar = "UPDATE cars "
                + "SET is_deleted = TRUE "
                + "WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteCar)) {
            statement.setLong(1, id);
            deleteDriversFromCar(get(id).orElseThrow());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car in database by id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getByDriver = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                + " m.name AS manufacturer_name, country "
                + "FROM cars_drivers cd "
                + "JOIN cars c ON c.id = cd.car_id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE AND cd.driver_id = ?;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getByDriver)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from database.", e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        manufacturer.setId(manufacturerId);
        Long carId = resultSet.getObject("car_id", Long.class);
        String carModel = resultSet.getString("model");
        return new Car(carId, carModel, manufacturer, getDriversForCar(carId));
    }

    private List<Driver> getDriversForCar(Long carId) {
        List<Driver> drivers = new ArrayList<>();
        String getDriverByCarId = "SELECT driver_id "
                + "FROM cars_drivers cd "
                + "WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(getDriverByCarId)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long driverId = resultSet.getObject(1, Long.class);
                drivers.add(driverDao.get(driverId).orElseThrow());
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers by id: " + carId, e);
        }
    }

    private Car deleteDriversFromCar(Car car) {
        String deleteDriver = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteDriver)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
            return get(car.getId()).orElseThrow();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't write relation car-driver to database,"
                    + " with car: " + car, e);
        }
    }

    private Car insertDriversToCar(Car car) {
        String addDriver = "INSERT INTO cars_drivers(car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(addDriver)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't write relation car-driver to database,"
                    + " with car: " + car, e);
        }
    }
}
