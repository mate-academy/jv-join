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
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertRequest = "INSERT INTO cars (model, manufacturer_id) values (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(insertRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                car.setId(generatedKeys.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert manufacturer to DB", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT c.id As car_id, model, m.id As manufacturer_id, "
                + "name, country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id where c.id = ? AND c.deleted = FALSE ;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCar(resultSet);
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't find car in DB by id: " + id, throwables);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "name, country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement =
                        connection.prepareStatement(getAllCarsQuery)) {

            ResultSet resultSet = getAllCarStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get all cars from DB", throwables);
        }
        addDriversForCar(carList);
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = False";
        int executeUpdate;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement =
                        connection.prepareStatement(updateQuery)) {
            softDeleteCarStatement.setString(1, car.getModel());
            softDeleteCarStatement.setLong(2, car.getManufacturer().getId());
            softDeleteCarStatement.setLong(3, car.getId());
            executeUpdate = softDeleteCarStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't update car from DB", throwables);
        }
        if (executeUpdate > 0) {
            removeDriversRelations(car);
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET deleted = True WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement softDeleteCarStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            softDeleteCarStatement.setLong(1, id);
            int numberOfDeletedRows = softDeleteCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't delete car with id: " + id, throwables);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT c.id as car_id, model, m.id as manufacturer_id, "
                + "name, country FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllByDriverStatement =
                        connection.prepareStatement(getAllByDriverRequest)) {
            getAllByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllByDriverStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCar(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get cars by driver id: "
                    + driverId, throwables);
        }
        addDriversForCar(carList);
        return carList;
    }

    private void addDriversForCar(List<Car> carList) {
        if (carList != null && carList.size() > 0) {
            for (Car car : carList) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
    }

    private Car parseCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car();
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        car.setId(resultSet.getObject("car_id", Long.class));
        return car;
    }

    private List<Driver> getDriversForCar(Long id) {
        String getAllDriversForCarRequest = "SELECT id, name, license_number FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id"
                + " WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversStatement =
                        connection.prepareStatement(getAllDriversForCarRequest)) {
            getAllDriversStatement.setLong(1, id);
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't find drivers in DB by car id", throwables);
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversToCar =
                        connection.prepareStatement(insertDriversQuery)) {
            addDriversToCar.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriversToCar.setLong(2, driver.getId());
                addDriversToCar.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert drivers to car: " + car, throwables);
        }
    }

    private void removeDriversRelations(Car car) {
        String removeRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeStatement = connection.prepareStatement(removeRequest)) {
            removeStatement.setLong(1, car.getId());
            removeStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't remove relations", throwables);
        }
    }
}
