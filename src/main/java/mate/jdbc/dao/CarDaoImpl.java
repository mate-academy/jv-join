package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    private CarService carService = new CarServiceImpl();

    @Override
    public Car create(Car car) {
        String query = "INSERT INTO taxy_service_cars(model, manufacturer_id)\n"
                + "VALUES(?,?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't create car. " + car, throwables);
        }
        if (car.getDrivers() != null) {
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT taxy_service_cars.id as car_id, model, "
                + "taxy_service_cars.manufacturer_id, manufacturers.name, manufacturers.country\n"
                + "FROM taxy_service.taxy_service_cars\n"
                + "JOIN manufacturers\n"
                + "on manufacturers.id = taxy_service_cars.manufacturer_id\n"
                + "WHERE taxy_service_cars.id = ? AND taxy_service_cars.is_deleted = false;";
        System.out.println(query);
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                            = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id  " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDrivers(id));
        }
        return car;
    }

    @Override
    public List<Car> getAll() throws SQLException {
        Car car = null;
        List<Car> cars = new ArrayList<>();
        List<Driver> drivers = new ArrayList<>();
        String query = "SELECT taxy_service_cars.id, "
                + "taxy_service_cars.model, taxy_service_cars.manufacturer_id, "
                + " manufacturers.name, manufacturers.country, "
                + "drivers.id, drivers.name, drivers.license_number "
                + "FROM taxy_service.taxy_service_cars "
                + "JOIN manufacturers "
                + "ON manufacturers.id = manufacturer_id "
                + "JOIN cars_drivers "
                + "ON car_id = taxy_service_cars.id "
                + "JOIN drivers "
                + "ON driver_id = drivers.id "
                + "order by taxy_service_cars.id;";
        ResultSet resultSet = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars", e);
        }
        for (Car c : cars) {
            c.setDrivers(getDrivers(c.getId()));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "update taxy_service_cars set model = ?, manufacturer_id = ? "
                + "where taxy_service_cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't udate car by id " + car.getId(), e);
        }
        for (Driver d: car.getDrivers()) {
            carService.removeDriverFromCar(d,car);
        }
        for (Driver d: car.getDrivers()) {
            carService.addDriverToCar(d,car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE taxy_service_cars SET is_deleted = true WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            int numberOfDeletedRows = statement.executeUpdate();
            return numberOfDeletedRows != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "select  car_id, taxy_service_cars.model,"
                + " manufacturer_id, manufacturers.name, country "
                + "from cars_drivers "
                + "join drivers "
                + "on driver_id = drivers.id "
                + "join taxy_service_cars "
                + "on taxy_service_cars.id = car_id "
                + "join manufacturers "
                + "on manufacturer_id = manufacturers.id "
                + "where driver_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while ((resultSet.next())) {
                Car car = getCar(resultSet);
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars by driverId " + driverId, e);
        }

        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer;
        Long carId = resultSet.getObject(1, Long.class);
        String model = resultSet.getString(2);
        Long manufacturerId = resultSet.getObject(3, Long.class);
        String manufacturerName = resultSet.getString(4);
        String manufacturerCountry = resultSet.getString(5);
        manufacturer = new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        return new Car(carId, model, manufacturer);
    }

    private List<Driver> getDrivers(Long carId) {

        String query = "SELECT taxy_service_cars.id, model, "
                + "taxy_service_cars.manufacturer_id, "
                + " manufacturers.name, manufacturers.country, "
                + "drivers.id, drivers.name, drivers.license_number "
                + "FROM taxy_service.taxy_service_cars "
                + "JOIN manufacturers "
                + "ON manufacturers.id = manufacturer_id "
                + "JOIN cars_drivers "
                + "ON car_id = taxy_service_cars.id "
                + "JOIN drivers "
                + "ON driver_id = drivers.id "
                + "WHERE taxy_service_cars.id = ?;";
        System.out.println(query);
        List<Driver> drivers = new ArrayList<>();
        Driver driver = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                driver = new Driver();
                driver.setId(resultSet.getObject("drivers.id", Long.class));
                driver.setName(resultSet.getString("drivers.name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                drivers.add(driver);
            }

        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by id  " + carId, e);
        }
        return drivers;
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("drivers.id", Long.class));
        driver.setName(resultSet.getString("drivers.name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String query = "insert into cars_drivers(car_id,driver_id) "
                + "values(?,?)";
        System.out.println(query);
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't insert drivers by car  " + car.getId(), e);
        }
    }

    private Driver getDriver(Long carId) {
        Driver driver = new Driver();
        String query = "SELECT drivers.id, drivers.name, "
                + "license_number  FROM taxy_service.drivers "
                + "JOIN cars_drivers "
                + "ON drivers.id = driver_id "
                + "WHERE car_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                driver.setId(resultSet.getObject("drivers.id", Long.class));
                driver.setName(resultSet.getString("drivers.name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car  " + carId, e);
        }
        return driver;
    }
}
