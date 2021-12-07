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
        String insertRequest = "INSERT INTO `cars` (model, manufacturer_id) VALUE (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement =
                         connection.prepareStatement(insertRequest,
                                 Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1,car.getModel());
            createCarStatement.setLong(2,car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert " + car + " to DB", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarRequest = "SELECT cars.id AS car_id, model, "
                + "manufacturers.id AS manufacturer_id, "
                + "country, name FROM cars INNER JOIN "
                + "manufacturers ON manufacturers.id = cars.manufacturer_id WHERE cars.id = ? "
                + "AND cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarByIdStatement =
                         connection.prepareStatement(getCarRequest)) {
            getCarByIdStatement.setLong(1, id);
            ResultSet resultSet = getCarByIdStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get №" + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarsRequest = "SELECT cars.id AS car_id, model, manufacturer_id, "
                + "country, name FROM cars INNER JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement =
                         connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from DB", e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement =
                         connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update " + car, e);
        }
        deleteDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement =
                         connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete car from DB by car id " + id, e);
        }
    }

    @Override
    public List<Car> getAllCarsByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getCarsByDriverRequest = "SELECT cars.id AS car_id, cars.model,"
                + " cars.manufacturer_id, manufacturers.country, manufacturers.name"
                + " FROM cars JOIN manufacturers ON manufacturers.id = cars.manufacturer_id"
                + " JOIN cars_drivers ON cars.id = cars_drivers.cars_id"
                + " WHERE drivers_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarsByDriverStatement =
                         connection.prepareStatement(getCarsByDriverRequest)) {
            getCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(parseCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get all cars from DB by driver id "
                    + driverId, e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
        return cars;
    }
    
    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO `cars_drivers` "
                + "(cars_id, drivers_id) VALUE (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriversStatement =
                         connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t insert drivers to " + car, e);
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(parseManufacturer(resultSet));
        return car;
    }
    
    private Manufacturer parseManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("manufacturer_id", Long.class);
        String name = resultSet.getString("name");
        String country = resultSet.getString("country");
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(id);
        return manufacturer;
    }
    
    private Driver parseDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }
    
    private List<Driver> getDriversForCar(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String getDriversRequest = "SELECT id, name, license_number "
                    + "FROM drivers "
                    + "INNER JOIN cars_drivers on drivers.id = cars_drivers.drivers_id "
                    + "WHERE cars_drivers.cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getDriversStatement =
                         connection.prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t get drivers for car №" + id, e);
        }
        return drivers;
    }
    
    private void deleteDrivers(Long id) {
        String query = "DELETE FROM cars_drivers WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                  PreparedStatement deleteDriversStatement
                          = connection.prepareStatement(query)) {
            deleteDriversStatement.setLong(1, id);
            deleteDriversStatement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t delete drivers from car №" + id, e);
        }
    }
}
