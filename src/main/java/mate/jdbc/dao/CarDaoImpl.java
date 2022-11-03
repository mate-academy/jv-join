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

    private static final String GET_CAR_REQUEST = "SELECT cars.id AS car_id, "
            + "cars.model AS car_model, manufacturers.id AS manufacturer_id, "
            + "manufacturers.name AS manufacturer_name, "
            + "manufacturers.country AS manufacturer_country "
            + "FROM cars JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
            + "JOIN cars_drivers ON cars_drivers.car_id = cars.id "
            + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarstatement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarstatement.setString(1, car.getModel());
            createCarstatement.setLong(2, car.getManufacturer().getId());
            createCarstatement.executeUpdate();
            ResultSet resultSet = createCarstatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            insertDriversIdToBetweenTable(car);
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + " in DB. ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_CAR_REQUEST)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            List<Driver> drivers = getAllDriversByCarIdFromDB(id);
            car.setDrivers(drivers);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String allCarsRequest = GET_CAR_REQUEST.replace("cars.id = ? AND", "");
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(allCarsRequest)) {
            ResultSet resultSet = statement.executeQuery();
            cars = getAllCarsWithManufacturersFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB ", e);
        }
        return getAllCarsWithDrivers(cars);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String allCarsByDriverRequest = GET_CAR_REQUEST.replace("cars.id =",
                "cars_drivers.driver_id =");
        List<Car> cars;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(allCarsByDriverRequest)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            cars = getAllCarsWithManufacturersFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars from DB by DriverId "
                    + driverId, e);
        }
        return getAllCarsWithDrivers(cars);
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest
                = "UPDATE cars SET model = ? , manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(updateCarRequest)) {
            statement.setString(1,car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update Car with id "
                    + car.getId() + " into DB",e);
        }
        deleteDriversIdFromBetweenTable(car);
        insertDriversIdToBetweenTable(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery
                = "UPDATE cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(deleteCarQuery)) {
            statement.setLong(1,id);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete Car with id "
                    + id + " into DB",e);
        }
    }

    private List<Car> getAllCarsWithManufacturersFromResultSet(ResultSet resultSet)
            throws SQLException {
        List<Car> cars = new ArrayList<>();
        while (resultSet.next()) {
            Car car = getCarWithManufacturerFromResultSet(resultSet);
            cars.add(car);
        }
        return cars;
    }

    private List<Driver> getAllDriversByCarIdFromDB(Long carId) {
        String getAllDriversRequest = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON cars_drivers.driver_id = drivers.id "
                + "WHERE  cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(getAllDriversRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject("id", Long.class));
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by  car id " + carId, e);
        }
    }

    private Car getCarWithManufacturerFromResultSet(ResultSet resultSet)
            throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private boolean deleteDriversIdFromBetweenTable(Car car) {
        String deleteDriversByCarIdRequest =
                "DELETE FROM cars_drivers WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(deleteDriversByCarIdRequest)) {
            statement.setLong(1, car.getId());
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete Drivers by Car's id into DB ",e);
        }
    }

    private boolean insertDriversIdToBetweenTable(Car car) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
            }
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver  id into DB ",e);
        }
    }

    private List<Car> getAllCarsWithDrivers(List<Car> cars) {
        for (Car car : cars) {
            List<Driver> allDriversByCarId = getAllDriversByCarIdFromDB(car.getId());
            car.setDrivers(allDriversByCarId);
        }
        return cars;
    }
}
