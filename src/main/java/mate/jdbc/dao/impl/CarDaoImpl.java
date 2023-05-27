package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertRequest =
                "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKey = createCarStatement.getGeneratedKeys();
            if (generatedKey.next()) {
                Long id = generatedKey.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert from to DB", e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Car get(Long id) {
        String selectRequest =
                "SELECT c.id as car_id, model, m.id as manufacturer_id, m.name, m.country "
                        + "FROM CARS c JOIN manufacturers m "
                        + "ON c.manufacturer_id = m.id "
                        + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarWithManufacturerResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find car in DB by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversFromCar(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() {
        Car car;
        List<Car> carList = new ArrayList<>();

        String selectGetAllRequest = "SELECT c.id as car_id, model, m.id as manufacturer_id,"
                + " m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(
                        selectGetAllRequest, Statement.RETURN_GENERATED_KEYS)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = parseCarWithManufacturerResultSet(resultSet);
                carList.add(car);
            }
            for (Car valueCar : carList) {
                valueCar.setDrivers(getDriversFromCar(valueCar.getId()));
            }
            return carList;
        } catch (SQLException throwable) {
            throw new RuntimeException(" is not good connection method getAll ", throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connect = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connect.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new RuntimeException(" is not good connection in method update ", throwable);
        }
        deleteCarsDrivers(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeletedCarStatement = connection.prepareStatement(
                        deleteCarQuery)) {
            softDeletedCarStatement.setLong(1, id);
            int numberOfDeletedRows = softDeletedCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        Car car;
        List<Car> carList = new ArrayList<>();

        String getAllCarsFromDriverRequest = "SELECT c.id as car_id, model, m.id as "
                + "manufacturer_id, m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id and c.is_deleted = FALSE "
                + "JOIN cars_drivers "
                + "ON c.id = cars_drivers.car_id WHERE cars_drivers.driver_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement =
                        connection.prepareStatement(getAllCarsFromDriverRequest)) {
            getCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            while (resultSet.next()) {
                car = parseCarWithManufacturerResultSet(resultSet);
                car.setDrivers(getDriversFromCar(car.getId()));
                carList.add(car);
            }
            return carList;
        } catch (SQLException throwable) {
            throw new RuntimeException(" is not good connection method getAllByDriver ", throwable);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCarStatement = connection.prepareStatement(
                        insertDriversQuery)) {
            addDriversToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCarStatement.setLong(2, driver.getId());
                addDriversToCarStatement.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers to car: " + car, e);
        }
    }

    private Car parseCarWithManufacturerResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setCountry(resultSet.getString("country"));
        manufacturer.setName(resultSet.getString("name"));
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("licenseNumber"));
        return driver;
    }

    private void deleteCarsDrivers(Car car) {
        String queryDeleted = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connect = ConnectionUtil.getConnection();
                PreparedStatement preparedStatement = connect.prepareStatement(queryDeleted)) {
            preparedStatement.setLong(1, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new RuntimeException(" is not good connection in method update:"
                    + " failed to remove drivers ", throwable);
        }
    }

    private List<Driver> getDriversFromCar(Long carId) {
        String getAllDriversFromCarRequest = "SELECT id, name, licenseNumber "
                + "FROM drivers d JOIN cars_drivers cd ON d.id = cd.driver_id "
                + "WHERE cd.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversFromCarRequest)) {
            getAllDriversStatement.setLong(1, carId);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find drivers in DB by car carId " + carId, e);
        }
    }
}
