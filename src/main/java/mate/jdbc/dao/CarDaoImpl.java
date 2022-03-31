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
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create "
                + car + ". ", e);
        }
        if (car.getDrivers() != null) {
            for (Driver driver : car.getDrivers()) {
                addDriversForCar(car, driver);
            }
        }
        return car;
    }

    @Override
    public Car get(Long id) {
        String query = "SELECT cars.id,model, manufacturers.name,"
                + "manufacturers.id,manufacturers.country "
                + "FROM taxi_db.cars "
                + "INNER JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? and cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturers(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        car.setDrivers(getListOfDriversByCar(car));
        return car;
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT cars.id,model,"
                + "manufacturers.name,manufacturers.id,manufacturers.country "
                + "FROM taxi_db.cars "
                + "INNER JOIN manufacturers "
                + "ON manufacturer_id = manufacturers.id ? "
                + "WHERE car.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturers(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars ", e);
        }
        for (Car car : cars) {
            car.setDrivers(getListOfDriversByCar(car));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        deleteDeleteDriversForCar(car);
        String query = "update cars "
                + "set model = ?,manufacturer_id = ? "
                + "where id = ? and cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, car.getModel());
            statement.setLong(2,car.getManufacturer().getId());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car, e);
        }
        for (Driver driver : car.getDrivers()) {
            addDriversForCar(car, driver);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        deleteDeleteDriversForCar(get(id));
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ? and cars.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id "
                + "from cars_drivers "
                + "inner join cars "
                + "on car_id = cars.id "
                + "where driver_id = ? ;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(get(resultSet.getLong("cars.id")));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars ", e);
        }
        return cars;
    }

    private void addDriversForCar(Car car, Driver driver) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,driver.getId());
            statement.setLong(2,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add drivers for "
                + car + ". ", e);
        }
    }

    private void deleteDeleteDriversForCar(Car car) {
        String query = "delete from cars_drivers where car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers for  "
                + car + ". ", e);
        }
    }

    private Car getCarWithManufacturers(ResultSet resultSet) throws SQLException {
        Long idOfCar = resultSet.getObject("cars.id", Long.class);
        String modelOfCar = resultSet.getString("model");
        Long idOfManufacturer = resultSet.getLong("manufacturers.id");
        String nameOfManufacturer = resultSet.getString("manufacturers.name");
        String countryOfManufacturer = resultSet.getString("manufacturers.country");
        Manufacturer manufacturer = new Manufacturer(idOfManufacturer,
                nameOfManufacturer,countryOfManufacturer);
        Car car = new Car(idOfCar,modelOfCar,manufacturer);
        return car;
    }

    private List<Driver> getListOfDriversByCar(Car car) {
        String query = "SELECT drivers.id,name,drivers.license_number "
                + "from cars_drivers "
                + "inner join drivers "
                + "on driver_id = drivers.id "
                + "where car_id = ? and drivers.is_deleted = 0;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long driverId = resultSet.getLong("drivers.id");
                String driverName = resultSet.getString("name");
                String driverLicenseNumber = resultSet.getString("drivers.license_number");
                Driver driver = new Driver(driverName,driverLicenseNumber);
                driver.setId(driverId);
                drivers.add(driver);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers by car " + car, e);
        }
        return drivers;
    }
}
