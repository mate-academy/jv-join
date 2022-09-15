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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertCarQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertCarStatement
                        = connection.prepareStatement(insertCarQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setString(1, car.getModel());
            insertCarStatement.setLong(2, car.getManufacturer().getId());
            insertCarStatement.executeUpdate();
            ResultSet generatedKeys = insertCarStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't create a new car from: " + car, e);
        }
        insertRelations(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarQuery = "SELECT cars.id, model,"
                + " manufacturers.id AS manufacturer_id,"
                + " manufacturers.name AS manufacturer_name,"
                + " manufacturers.country AS manufacturer_country"
                + " FROM cars"
                + " JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement
                        = connection.prepareStatement(getCarQuery)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get from DB by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllCarsQuery = "SELECT cars.id, model,"
                + " manufacturers.id AS manufacturer_id,"
                + " manufacturers.name AS manufacturer_name,"
                + " manufacturers.country AS manufacturer_country"
                + " FROM cars"
                + " JOIN manufacturers"
                + " ON cars.manufacturer_id = manufacturers.id"
                + " WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsQuery)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all cars from DB", e);
        }
        if (!cars.isEmpty()) {
            cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement
                        = connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't update from DB by car" + car, e);
        }
        deleteRelation(car);
        insertRelations(car);
        return null;
    }

    @Override
    public boolean delete(Long carId) {
        String deleteCarQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement
                        = connection.prepareStatement(deleteCarQuery)) {
            deleteCarStatement.setLong(1,carId);
            int updatedRows = deleteCarStatement.executeUpdate();
            return updatedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car from DB by id: " + carId, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverQuery = "SELECT id, model"
                + " FROM cars"
                + " JOIN cars_drivers"
                + " ON cars.id = cars_drivers.car_id"
                + " WHERE cars_drivers.driver_id = ?"
                + " AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get cars from DB by driver id " + driverId, e);
        }
    }

    private void insertRelations(Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement
                        = connection.prepareStatement(insertDriversQuery,
                        Statement.RETURN_GENERATED_KEYS)) {
            addDriverToCarStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                addDriverToCarStatement.setLong(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers to car " + car, e);
        }
    }

    private void deleteRelation(Car car) {
        String deleteRelationQuery = "DELETE FROM cars_drivers "
                + " WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationStatement
                        = connection.prepareStatement(deleteRelationQuery)) {
            deleteRelationStatement.setObject(1, car.getId());
            deleteRelationStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car from DB " + car, e);
        }
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDriversForCarQuery = "SELECT id, name, license_number"
                + " FROM drivers"
                + " JOIN cars_drivers"
                + " ON drivers.id = cars_drivers.driver_id"
                + " WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriversForCarStatement
                        = connection.prepareStatement(getAllDriversForCarQuery)) {
            getAllDriversForCarStatement.setObject(1,carId);
            ResultSet resultSet = getAllDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get drivers from DB for car id" + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver();
        driver.setId(id);
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    private Car getCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Manufacturer manufacturer
                = new Manufacturer(resultSet.getString("manufacturer_name"),
                resultSet.getString("manufacturer_country"));
        manufacturer.setId(resultSet.getObject("manufacturer_id",Long.class));
        return new Car(id,model,manufacturer);
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        return car;
    }
}
