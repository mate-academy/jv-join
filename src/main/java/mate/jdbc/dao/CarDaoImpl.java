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
        String createCarRequest = "INSERT INTO cars (model, manufacturers_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement insertCarStatement =
                                connection.prepareStatement(createCarRequest,
                                        Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setLong(2, car.getManufacturer().getId());
            insertCarStatement.executeUpdate();
            ResultSet resultSet = insertCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create "
                    + car + ". ", throwable);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT cars.id, cars.model, cars.manufacturers_id, name, country"
                + " FROM cars JOIN manufacturers ON manufacturers.id = cars.manufacturers_id"
                + " WHERE cars.id = ? AND cars.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement getCarStatement =
                                connection.prepareStatement(getCarRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDriverList(getDriverForCar(id));
        }
        return Optional.ofNullable(car);

    }

    @Override
    public List<Car> getAll() {
        String getAllCarsRequest = "SELECT c.id as id, model, m.id as manufacturers_id, "
                + "m.name, m.country"
                + " FROM cars c JOIN manufacturers m"
                + " ON c.manufacturers_id = m.id"
                + " WHERE c.is_deleted = FALSE";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                         PreparedStatement getAllCarsStatement =
                                 connection.prepareStatement(getAllCarsRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from carsDB.",
                    throwable);
        }
        carList.forEach(car -> car.setDriverList(getDriverForCar(car.getId())));
        return carList;

    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET model = ?, manufacturers_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement updateCarStatement
                                = connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update "
                    + car + throwable);
        }
        softDeleteRelationsForCar(car);
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement deleteCarStatement =
                                connection.prepareStatement(deleteCarRequest)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT c.id AS id, c.model, c.manufacturers_id,"
                + " m.name, m.country"
                + " FROM cars c JOIN manufacturers m"
                + " ON c.manufacturers_id = m.id"
                + " JOIN cars_drivers cd ON c.id = cd.car_id"
                + " WHERE cd.driver_id = ?";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement getAllCarsByDriverStatement =
                                connection.prepareStatement(getAllByDriverRequest)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            while (resultSet.next()) {
                carList.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Couldn't get drivers with id "
                    + driverId, throwables);
        }
        carList.forEach(car -> car.setDriverList(getDriverForCar(car.getId())));
        return carList;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement addDriverToCarStatement =
                                connection.prepareStatement(insertDriversRequest)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver: car.getDriverList()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert driver to car " + car, e);
        }
    }

    private void softDeleteRelationsForCar(Car car) {
        String softDeleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement deleteStatement =
                                connection.prepareStatement(softDeleteDriversRequest)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to remove relation for car " + car, e);
        }
    }

    private List<Driver> getDriverForCar(Long id) {
        String getDriversForCarRequest = "SELECT d.id, name, license_number"
                + " FROM drivers d JOIN cars_drivers cd"
                + " ON d.id = cd.driver_id"
                + " WHERE cd.car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement getDriversStatement =
                                connection.prepareStatement(getDriversForCarRequest)) {
            getDriversStatement.setLong(1, id);
            ResultSet resultSet = getDriversStatement.executeQuery();
            List<Driver> driverList = new ArrayList<>();
            while (resultSet.next()) {
                driverList.add(getDriverFromResultSet(resultSet));
            }
            return driverList;
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't get drivers from DB by car id "
                    + id, throwables);
        }
    }

    private Car getCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer();
        Car car = new Car(name, manufacturer);
        car.setId(id);
        manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return car;
    }

    private Driver getDriverFromResultSet(ResultSet resultSet) throws SQLException {
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }
}
