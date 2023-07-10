package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarDao;
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
        String createCarQuery = "INSERT INTO cars (model, manufacturer_id) "
                                + "VALUES (?, ?);";
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement =
                         connection.prepareStatement(createCarQuery,
                                                     Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
            
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car " + car, e);
        }
        
        insertDriverToCar(car);
        return car;
    }
    
    @Override
    public Optional<Car> get(Long id) {
        String getCarWithManufacturerQuery = "SELECT c.id AS car_id, model, "
                             + "m.id AS manufacturer_id, m.name, m.country "
                             + "FROM cars AS c "
                             + "INNER JOIN manufacturers AS m "
                             + "ON c.manufacturer_id = m.id "
                             + "WHERE c.id = ?;";
        Car car = null;
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarWithManufacturerStatement =
                        connection.prepareStatement(getCarWithManufacturerQuery)) {
            getCarWithManufacturerStatement.setLong(1, id);
            ResultSet resultSet = getCarWithManufacturerStatement.executeQuery();
        
            if (resultSet.next()) {
                car = parseCarWithManufacturerFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car with manufacturer by id: " + id, e);
        }
        
        if (car != null) {
            car.setDrivers(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }
    
    @Override
    public List<Car> getAll() {
        String getAllCarQuery = "SELECT c.id AS car_id, model, m.id AS manufacturer_id, "
                                + "m.name, m.country "
                                + "FROM cars AS c "
                                + "JOIN manufacturers AS m "
                                + "ON c.manufacturer_id = m.id "
                                + "WHERE c.is_deleted = FALSE;";
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarStatement =
                        connection.prepareStatement(getAllCarQuery)) {
            ResultSet resultSet = getAllCarStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            
            while (resultSet.next()) {
                Car car = parseCarWithManufacturerFromResultSet(resultSet);
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars from DB", e);
        }
    }
    
    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars "
                                + "SET model = ?, manufacturer_id = ?, is_deleted = FALSE "
                                + "WHERE id = ?;";
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarQuery)) {
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setLong(2, car.getManufacturer().getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car from DB by id: " + car.getId(), e);
        }
        
        deleteRelationsInCarsDriversTable(car.getId());
        insertDriverToCar(car);
        return car;
    }
    
    @Override
    public boolean delete(Long id) {
        String deleteCarQuery = "UPDATE cars "
                                + "SET is_deleted = TRUE "
                                + "WHERE id = ?;";
        int changedRows;
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteCarAStatement =
                        connection.prepareStatement(deleteCarQuery)) {
            deleteCarAStatement.setLong(1, id);
            changedRows = deleteCarAStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car from DB by id: " + id, e);
        }
        
        return changedRows != 0 && deleteRelationsInCarsDriversTable(id);
    }
    
    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllCarsByDriverQuery = "SELECT DISTINCT c.id AS car_id, model, "
                                         + "m.id AS manufacturer_id, m.name, m.country "
                                         + "FROM cars AS c "
                                         + "JOIN cars_drivers AS cd ON c.id = cd.car_id "
                                         + "JOIN manufacturers AS m ON c.manufacturer_id = m.id "
                                         + "WHERE cd.driver_id = ? "
                                         + "AND cd.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsByDriverStatement =
                        connection.prepareStatement(getAllCarsByDriverQuery)) {
            getAllCarsByDriverStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsByDriverStatement.executeQuery();
            
            while (resultSet.next()) {
                cars.add(parseCarWithManufacturerFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars by driver id: " + driverId, e);
        }
        
        return cars;
    }
    
    private void insertDriverToCar(Car car) {
        String insertDriverQuery = "INSERT INTO cars_drivers (driver_id, car_id) "
                                   + "VALUES (?, ?);";
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement =
                        connection.prepareStatement(insertDriverQuery)) {
            insertDriverStatement.setLong(2, car.getId());
            
            for (Driver driver : car.getDrivers()) {
                insertDriverStatement.setLong(1, driver.getId());
                insertDriverStatement.executeUpdate();
            }
            
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver to car " + car, e);
        }
    }
    
    private List<Driver> getDriverForCar(Long carId) {
        String getDriverQuery = "SELECT id, name, license_number "
                                + "FROM drivers AS d "
                                + "JOIN cars_drivers AS cd "
                                + "ON d.id = cd.driver_id "
                                + "WHERE car_id = ? AND cd.is_deleted = FALSE;";
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverStatement =
                        connection.prepareStatement(getDriverQuery)) {
            getDriverStatement.setLong(1, carId);
            ResultSet resultSet = getDriverStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get driver with car by id: " + carId, e);
        }
    }
    
    private Car parseCarWithManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        car.setManufacturer(manufacturer);
        
        return car;
    }
    
    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }
    
    private boolean deleteRelationsInCarsDriversTable(Long carId) {
        String deleteRelationsQuery = "UPDATE cars_drivers "
                                      + "SET is_deleted = TRUE "
                                      + "WHERE car_id = ?;";
        
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement =
                        connection.prepareStatement(deleteRelationsQuery)) {
            deleteRelationsStatement.setLong(1, carId);
            return deleteRelationsStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations from DB by id: "
                                              + carId, e);
        }
    }
}
