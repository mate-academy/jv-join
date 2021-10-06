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
        String sqlQuery = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                        connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setObject(2, car.getManufacturer().getId());
            createCarStatement.execute();
            ResultSet resultSetUpdate = createCarStatement.getGeneratedKeys();
            if (resultSetUpdate.next()) {
                car.setId(resultSetUpdate.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car with param: " + car + ". ", e);
        }
        if (car.getDriverList() != null) {
            insertNewRelation(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String sqlQuery = "SELECT c.id AS car_id, c.model AS car_modal, "
                + "m.id AS manufacturers_id, m.name AS manufacturers_name,"
                + "m.country AS manufacturers_country "
                + "FROM cars AS c JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car carById = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getByIdStatement = connection.prepareStatement(sqlQuery)) {
            getByIdStatement.setObject(1, id);
            ResultSet resultSetGetById = getByIdStatement.executeQuery();
            while (resultSetGetById.next()) {
                carById = getCar(resultSetGetById);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id " + id + ". ", e);
        }

        if (carById != null) {
            carById.setDriverList(getDriversForCar(id));
        }
        return Optional.ofNullable(carById);
    }

    @Override
    public List<Car> getAll() {
        String sqlQuery = "SELECT c.id AS car_id, c.model AS car_modal, "
                + "m.id AS manufacturers_id, m.name AS manufacturers_name, "
                + "m.country AS manufacturers_country "
                + "FROM cars AS c JOIN manufacturers AS m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> carList = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(sqlQuery)) {
            ResultSet resultSetGetAllCars = getAllCarsStatement.executeQuery();
            while (resultSetGetAllCars.next()) {
                carList.add(getCar(resultSetGetAllCars));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars. ", e);
        }
        for (Car car : carList) {
            car.setDriverList(getDriversForCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String sqlQuery = "UPDATE cars SET model = ?, manufacturer_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE ;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setObject(3, car.getId());
            updateCarStatement.executeUpdate();
            ResultSet resultSetUpdateCar = updateCarStatement.getGeneratedKeys();
            if (resultSetUpdateCar.next()) {
                car.setId(resultSetUpdateCar.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update with param: " + car + ". ", e);
        }
        deleteOldRelation(car);
        insertNewRelation(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id " + id, e);
        }
    }

    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id AS car_id, c.model AS car_modal, "
                + "m.id AS manufacturers_id, m.name AS manufacturers_name, "
                + "m.country AS manufacturers_country "
                + "FROM cars AS c "
                + "JOIN cars_drivers ON cars_drivers.car_id = c.id "
                + "JOIN manufacturers AS m ON m.id = c.manufacturer_id "
                + "WHERE cars_drivers.driver_id = ? AND c.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsStatement = connection.prepareStatement(query)) {
            getCarsStatement.setObject(1, driverId);
            ResultSet resultSet = getCarsStatement.executeQuery();
            List<Car> carsByDriver = new ArrayList<>();
            Car car;
            while (resultSet.next()) {
                car = getCar(resultSet);
                carsByDriver.add(car);
            }
            for (Car drivers : carsByDriver) {
                drivers.setDriverList(getDriversForCar(drivers.getId()));
            }
            return carsByDriver;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id: "
                    + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_modal"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturers_name"));
        manufacturer.setCountry(resultSet.getString("manufacturers_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long catId) {
        String sqlQuery = "SELECT DISTINCT d.id AS driver_id, d.name AS driver_name, "
                + "d.license_number AS driver_license_number "
                + "FROM drivers AS d JOIN cars_drivers AS cd on d.id = cd.driver_id "
                + "WHERE cd.car_id = ? AND d.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getListDriverStatement = connection.prepareStatement(sqlQuery)) {
            getListDriverStatement.setObject(1, catId);
            ResultSet resultSetListDrivers = getListDriverStatement.executeQuery();
            List<Driver> listDrivers = new ArrayList<>();
            while (resultSetListDrivers.next()) {
                listDrivers.add(getDriver(resultSetListDrivers));
            }
            return listDrivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get list drivers with car is "
                    + catId + ". ", e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("driver_id", Long.class));
        driver.setName(resultSet.getString(2));
        driver.setLicenseNumber(resultSet.getString(3));
        return driver;
    }

    private void deleteOldRelation(Car car) {
        String sqlQuery = "DELETE FROM cars_drivers WHERE car_id = ?; ";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationStatement = connection.prepareStatement(sqlQuery)) {
            deleteRelationStatement.setObject(1, car.getId());
            deleteRelationStatement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete old relation from "
                    + "cars_drivers with param: " + car + ". ", e);
        }
    }

    private void insertNewRelation(Car car) {
        String sqlQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertRelationStatement = connection.prepareStatement(sqlQuery)) {
            insertRelationStatement.setObject(1, car.getId());
            for (Driver driver : car.getDriverList()) {
                insertRelationStatement.setObject(2, driver.getId());
                insertRelationStatement.execute();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert new relation to"
                    + " cars_drivers with param: " + car + ". ", e);
        }
    }
}
