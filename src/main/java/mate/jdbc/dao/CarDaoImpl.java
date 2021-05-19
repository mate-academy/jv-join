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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement
                        = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setObject(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't create car. " + car + " ",
                    throwable);
        }
        insertDriver(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String getRequest = "SELECT  c.id AS car_id, model, m.id , m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.id = ? AND c.deleted = FALSE ";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarStatement =
                        connection.prepareStatement(getRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert manufacturer "
                    + id, e);
        }
        if (car != null) {
            car.setDriver(getDriverForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllQuery = "SELECT c.id AS car_id, model, "
                + "m.id, m.name, m.country "
                + "FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement
                        = connection.prepareStatement(getAllQuery)) {
            List<Car> cars = new ArrayList<>();
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturer(resultSet));
            }
            return cars;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table. ",
                    throwable);
        }
    }

    @Override
    public Car update(Car car) {
        String updateQuery = "UPDATE cars SET model = ? , manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement
                        = connection.prepareStatement(updateQuery)) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setObject(2, car.getManufacturer().getId());
            updateStatement.setObject(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't update car: " + car.getModel()
                    + " from cars table. ",
                    throwable);
        }
        removeRelation(car);
        insertDriver(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteManufacturerStatement
                        = connection.prepareStatement(deleteQuery)) {
            deleteManufacturerStatement.setLong(1, id);
            return deleteManufacturerStatement.executeUpdate() != 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete a cars by id " + id + " ",
                    throwable);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT c.id as car_id, c.model as car_name, m.name as manufacturer_name,"
                + "m.id as manufacturer_id,"
                + "country as manufacturer_country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ?"
                + "AND c.deleted = FALSE;;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCars = connection.prepareStatement(query)) {
            getAllCars.setObject(1,driverId);
            ResultSet resultSet = getAllCars.executeQuery();
            while (resultSet.next()) {
                Car carWithManufacturer = getCarWithManufacturer(resultSet);
                List<Driver> driverForCar = getDriverForCar(carWithManufacturer.getId());
                carWithManufacturer.setDriver(driverForCar);
                cars.add(carWithManufacturer);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get all cars from DB by id ",
                    throwable);
        }
        return cars;
    }

    private List<Driver> getDriverForCar(Long driverId) {
        String getAllDriverForCarRequest = "SELECT * FROM drivers d "
                + "JOIN cars_drivers cd ON d.id = cd.driver_id WHERE d.id = ? AND deleted = false;";
        List<Driver> drivers = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllDriverStatement =
                        connection.prepareStatement(getAllDriverForCarRequest)) {
            getAllDriverStatement.setObject(1, driverId);
            ResultSet resultSet = getAllDriverStatement.executeQuery();
            DriverDaoImpl daoDriver = new DriverDaoImpl();
            while (resultSet.next()) {
                drivers.add(daoDriver.getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't get driver for car, id " + driverId, e);
        }
        return drivers;
    }

    private void insertDriver(Car car) {
        String insertQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement =
                         connection.prepareStatement(insertQuery)) {
            addDriverToCarStatement.setObject(1, car.getId());
            for (Driver driver : car.getDriver()) {
                addDriverToCarStatement.setObject(2, driver.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException throwables) {
            throw new DataProcessingException("Can't insert driver to a car "
                    + car + " ", throwables);
        }
    }

    private Car getCarWithManufacturer(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getObject("id", Long.class);
        Manufacturer manufacturer = new Manufacturer(resultSet.getString("name"),
                resultSet.getString("country"));
        manufacturer.setId(manufacturerId);
        String carModel = resultSet.getString("model");
        Long carId = resultSet.getObject("car_id", Long.class);
        Car car = new Car();
        car.setModel(carModel);
        car.setId(carId);
        car.setManufacturer(manufacturer);
        return car;
    }

    private void removeRelation(Car car) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ?;";
        try (Connection deleteConnection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement
                        = deleteConnection.prepareStatement(deleteQuery)) {
            deleteStatement.setObject(1, car.getId());
            deleteStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't deleted cars "
                    + "from cars table. ",
                    throwable);
        }
    }
}
