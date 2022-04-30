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
    /**
     * 1. Inserts car into `cars` table
     * 2. Sets car id by generated value
     * 3. Inserts car and cars driver into `cars_drivers` table
     *
     * @param car that will be saved to db
     * @return Car objected with seted generated id
     */
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO `cars` (manufacturer_id, model) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", e);
        }
        insertCarsDriversRecords(car);
        return car;
    }

    /**
     * 1. Gets car data from `cars` and creates car object
     * 2. Gets drivers list of this car from `cars_drivers`
     * 3. Sets drivers list into car
     *
     * @param id of needed car
     * @return Optional of car if exist, in other case Optional.empty()
     */
    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT cars.id , cars.model, cars.manufacturer_id, m.name, m.country "
                + "FROM cars JOIN manufacturers m ON cars.manufacturer_id = m.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car == null) {
            return Optional.empty();
        }
        injectDriversInEachCar(List.of(car));
        return Optional.of(car);
    }


    /**
     * 1. Gets all records from cars and creates car objects
     * 2. Gets drivers for each car and sets those
     *
     * @return list of cars from `cars` table
     */
    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id, m.name, m.country "
                + "FROM cars JOIN manufacturers m ON cars.manufacturer_id = m.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.", e);
        }
        injectDriversInEachCar(carList);
        return carList;
    }

    /**
     * 1. Updates car record in cars using given car fields
     * 2. Deletes records in `cars_drivers` with car_id == car.id
     * 3. Inserts records in `cars_drivers` which represents actual links
     *
     * @param car that will be updated in db
     * @return given car object
     */
    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturer_id = ? WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        deleteCarsDriversRecords(car);
        if (!car.getDrivers().isEmpty()) {
            insertCarsDriversRecords(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    //todo: keep or delete
    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String query = "INSERT INTO `cars_drivers` (driver_id, car_id) values (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driver.getId());
            statement.setLong(2, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    String.format("Couldn't update `cars_drivers` using car id %d "
                            + "and driver id %d", car.getId(), driver.getId()), e);
        }
        car.getDrivers().add(driver);
    }

    //todo: keep or delete
    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String query = "DELETE FROM cars_drivers WHERE driver_id = ? AND car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driver.getId());
            statement.setLong(2, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException(
                    String.format("Couldn't update `cars_drivers` using car id %d "
                            + "and driver id %d", car.getId(), driver.getId()), e);
        }
        car.getDrivers().remove(driver);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id, m.name, m.country "
                + "FROM cars INNER JOIN manufacturers m ON cars.manufacturer_id = m.id "
                + "INNER JOIN cars_drivers cd ON cd.car_id = cars.id "
                + "WHERE cd.driver_id = ?";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                carList.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car list by driver id "
                    + driverId, e);
        }
        injectDriversInEachCar(carList);
        return carList;
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        String model = resultSet.getObject("model", String.class);
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        String manufacturerName = resultSet.getObject("name", String.class);
        String manufacturerCountry = resultSet.getObject("country", String.class);
        Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
        Car car = new Car(model, manufacturer);
        manufacturer.setId(manufacturerId);
        car.setId(resultSet.getObject("cars.id", Long.class));
        return car;
    }

    private void deleteCarsDriversRecords(Car car) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete from `cars_drivers` by "
                    + car.getId(), e);
        }
    }

    private void insertCarsDriversRecords(Car car) {
        String query = "INSERT INTO `cars_drivers` (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(2, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(1, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create record with "
                    + car.getId() + " and " + car.getDrivers() + ". ", e);
        }
    }

    private void injectDriversInEachCar(List<Car> cars) {
        String query = "SELECT driver_id, name, license_number "
                + "FROM cars_drivers cd JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            for (Car car : cars) {
                statement.setLong(1, car.getId());
                ResultSet resultSet = statement.executeQuery();
                List<Driver> driverList = new ArrayList<>();
                while (resultSet.next()) {
                    driverList.add(parseDriverFromResultSet(resultSet));
                }
                car.setDrivers(driverList);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers of car in " + cars, e);
        }
    }

    //todo: keep or delete
    private List<Driver> getCarDrivers(Car car) {
        String query = "SELECT driver_id, name, license_number "
                + "FROM cars_drivers cd JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE car_id = ? AND d.is_deleted = FALSE;";
        List<Driver> driverList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                driverList.add(parseDriverFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers of car " + car, e);
        }
        return driverList;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getObject("name", String.class);
        String licenseNumber = resultSet.getObject("license_number", String.class);
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(resultSet.getObject("driver_id", Long.class));
        return driver;
    }
}
