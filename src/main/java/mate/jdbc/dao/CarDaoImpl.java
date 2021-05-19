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
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) "
                + "VALUES (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement
                        = connection.prepareStatement(createCarQuery,
                           Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet generatedKeys = createCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car. " + car
                    + " ", e);
        }
        addDriverRelationForCar(car);
        car.setDrivers(getDriversForCar(car.getId()));
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT c.id as car_id, c.model as car_model, "
                + "m.name as manufacturer_name, "
                + "m.id as manufacturer_id, m.country as manufacturer_country "
                + "FROM cars c JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB by id " + id + " ",
                    e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT c.id as car_id, c.model as car_model, "
                + "m.name as manufacturer_name, m.id as manufacturer_id, "
                + "country as manufacturer_country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                   PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from DB ",
                    e);
        }
        parseDriversToListOfCars(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB. ", e);
        }
        removeDriversRelations(car);
        List<Driver> drivers = car.getDrivers();
        if (drivers != null) {
            addDriverRelationForCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1, id);
            int numberOfDeletedRows = deleteCarStatement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id
                    + " from DB ", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverQuery = "SELECT c.model as car_model, c.id as car_id, "
                + "m.name as manufacturer_name, m.country as manufacturer_country, "
                + "m.id as manufacturer_id  FROM cars_drivers cd "
                + "LEFT JOIN cars c ON cd.car_id = c.id "
                + "LEFT JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "WHERE driver_id = ? AND c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement
                        = connection.prepareStatement(getAllByDriverQuery)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                Car car = getCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by drivers id " + driverId, e);
        }
        parseDriversToListOfCars(cars);
        return cars;
    }

    private void addDriverRelationForCar(Car car) {
        String addDriverRelationQuery = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverRelationStatement
                        = connection.prepareStatement(addDriverRelationQuery)) {
            addDriverRelationStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverRelationStatement.setLong(2, driver.getId());
                addDriverRelationStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers relations for car with id "
                    + car.getId(), e);
        }
    }

    private void removeDriversRelations(Car car) {
        String removeDriversRelationsQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriversRelationsStatement
                        = connection.prepareStatement(removeDriversRelationsQuery)) {
            removeDriversRelationsStatement.setLong(1, car.getId());
            removeDriversRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete drivers relations for car with id "
                    + car.getId(), e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getDriversForCarQuery = "SELECT d.id as driver_id, d.name as driver_name, "
                + "d.license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE cd.car_id = ? "
                + "AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement
                        = connection.prepareStatement(getDriversForCarQuery)) {
            getDriversForCarStatement.setLong(1, carId);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriversFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car with id " + carId,
                    e);
        }
    }

    private Car getCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setModel(resultSet.getString("car_model"));
        car.setId(resultSet.getObject("car_id", Long.class));
        Manufacturer manufacturer
                = new Manufacturer(resultSet.getString("manufacturer_name"),
                resultSet.getString("manufacturer_country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        car.setManufacturer(manufacturer);
        return car;
    }

    private void parseDriversToListOfCars(List<Car> cars) {
        if (cars.size() != 0) {
            for (Car car : cars) {
                car.setDrivers(getDriversForCar(car.getId()));
            }
        }
    }

    private Driver parseDriversFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver(resultSet.getString("driver_name"),
                resultSet.getString("license_number"));
        driver.setId(resultSet.getObject("driver_id", Long.class));
        return driver;
    }
}
