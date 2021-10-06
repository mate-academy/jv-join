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
    private static final String LICENSE_NUMBER_LITERAL = "license_number";
    private static final String NAME_LITERAL = "name";
    private static final String CAR_ID_LITERAL = "car_id";
    private static final String CAR_MODEL_LITERAL = "model";
    private static final String MANUFACTURER_ID_LITERAL = "manufacturer_id";
    private static final String MANUFACTURER_NAME_LITERAL = "manufacturer_name";
    private static final String MANUFACTURER_COUNTRY_LITERAL = "manufacturer_country";

    @Override
    public Car create(Car car) {
        String insertRequest = "insert into cars (model, manufacturer_id) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection
                        .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            while (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t create car: " + car, throwables);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT cars.id As car_id, cars.model, "
                + "manufacturers.name AS manufacturer_name, manufacturers.id AS manufacturer_id, "
                + "manufacturers.country AS manufacturer_country, "
                + "cars.is_deleted FROM cars JOIN manufacturers "
                + "ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get data by id: " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT c.id As car_id, c.model, "
                + "m.name AS manufacturer_name, "
                + "m.id AS manufacturer_id, "
                + "m.country AS manufacturer_country, "
                + "c.is_deleted "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection
                        .prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all cars from db", throwables);
        }
        for (Car car: cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?,"
                + " manufacturer_id = ? WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement = connection
                        .prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t update car " + car, throwables);
        }
        deleteFromCarDriverTable(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(deleteRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverRequest = "SELECT c.id AS car_id, c.model, "
                + "m.name AS manufacturer_name, "
                + "m.country AS manufacturer_country, "
                + "m.id AS manufacturer_id, "
                + "c.is_deleted "
                + "FROM cars c "
                + "JOIN cars_drivers cd "
                + "ON cd.car_id = c.id "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement = connection
                        .prepareStatement(getAllCarsByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get all cars by driver id: "
                    + driverId, throwables);
        }
        for (Car car: cars) {
            car.setDrivers(getDriversForCar(car.getId()));
        }
        return cars;
    }

    @Override
    public Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject(CAR_ID_LITERAL, Long.class);
        String model = resultSet.getString(CAR_MODEL_LITERAL);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject(MANUFACTURER_ID_LITERAL, Long.class));
        manufacturer.setName(resultSet.getString(MANUFACTURER_NAME_LITERAL));
        manufacturer.setCountry(resultSet.getString(MANUFACTURER_COUNTRY_LITERAL));
        Car car = new Car(id, model, manufacturer);
        return car;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString(NAME_LITERAL);
        String licenseNumber = resultSet.getString(LICENSE_NUMBER_LITERAL);
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriverRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES(?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement = connection
                        .prepareStatement(insertDriverRequest)) {
            insertDriverStatement.setLong(1,car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(2, driver.getId());
                insertDriverStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t insert Driver in car: " + car, throwables);
        }
    }

    private boolean deleteFromCarDriverTable(Car car) {
        String deleteCarDriverConnectionsRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteConnectionByCar = connection
                        .prepareStatement(deleteCarDriverConnectionsRequest)) {
            deleteConnectionByCar.setLong(1, car.getId());
            return deleteConnectionByCar.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t delete car-driver connection by car id: "
                    + car.getId(), throwables);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversRequest = "SELECT id, name, license_number "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement getDriversStatement = connection
                            .prepareStatement(getDriversRequest)) {
            getDriversStatement.setLong(1, carId);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can`t get driver by car id " + carId, throwables);
        }
    }
}
