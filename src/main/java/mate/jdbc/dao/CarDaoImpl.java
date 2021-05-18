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
        String createRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?)";
        boolean created = false;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement createCarStatement = connection.prepareStatement(createRequest,
                         Statement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setString(1, car.getModel());
            createCarStatement.setLong(2, car.getManufacturer().getId());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                created = true;
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't create "
                    + car + ". ", throwable);
        }
        if (created) {
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectRequest = "SELECT cars.id as car_id, model, manufacturer_id, "
                + "manufacturers.name, country FROM cars JOIN manufacturers ON "
                + "cars.manufacturer_id = manufacturers.id WHERE cars.id = ? "
                + "AND cars.deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getCarStatement = connection.prepareStatement(selectRequest)) {
            getCarStatement.setLong(1, id);
            ResultSet resultSet = getCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarWithManufacturer(resultSet);
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get car by id " + id, throwable);
        }
        if (car != null) {
            car.setDrivers(findDriversByCarId(car.getId()));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String getAllRequest = "SELECT cars.id as car_id, model, manufacturer_id, "
                + "manufacturers.name, manufacturers.country FROM cars JOIN manufacturers ON "
                + "cars.manufacturer_id = manufacturers.id WHERE cars.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllCarsStatement
                            = connection.prepareStatement(getAllRequest)) {
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.",
                    throwable);
        }
        if (cars != null) {
            cars.forEach(car -> car.setDrivers(findDriversByCarId(car.getId())));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarsRequest = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND deleted = FALSE";
        int updatedRows;
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement updateCarStatement
                         = connection.prepareStatement(updateCarsRequest)) {
            updateCarStatement.setString(1, car.getModel());
            updateCarStatement.setObject(2, car.getManufacturer().getId());
            updateCarStatement.setLong(3, car.getId());
            updatedRows = updateCarStatement.executeUpdate();
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't update "
                    + car + " in driversDB.", throwable);
        }
        if (updatedRows > 0) {
            deleteDrivers(car);
            insertDrivers(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteCarStatement = connection.prepareStatement(query)) {
            deleteCarStatement.setLong(1, id);
            return deleteCarStatement.executeUpdate() > 0;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't delete car with id " + id, throwable);
        }
    }

    @Override
    public List<Car> getAllByDriverID(Long driverId) {
        String findCarsRequest = "SELECT * FROM cars c "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id WHERE cd.driver_id = ? "
                + "AND c.deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement findAllCarsStatement
                            = connection.prepareStatement(findCarsRequest)) {
            findAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = findAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithManufacturer(resultSet));
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of cars from DB.",
                    throwable);
        }
        if (cars != null) {
            cars.forEach(car -> car.setDrivers(findDriversByCarId(car.getId())));
        }
        return cars;
    }

    private void deleteDrivers(Car car) {
        String deleteDriversRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement deleteDriversStatement
                         = connection.prepareStatement(deleteDriversRequest)) {
            for (Driver driver : car.getDrivers()) {
                deleteDriversStatement.setLong(1, driver.getId());
                deleteDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't delete drivers to DB for car " + car,
                    throwable);
        }
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) "
                + "VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement insertDriversStatement
                         = connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't insert drivers to DB for car " + car,
                    throwable);
        }
    }

    private Car getCarWithManufacturer(ResultSet resultSet) {
        try {
            Long carId = resultSet.getObject("car_id", Long.class);
            String carModel = resultSet.getString("model");
            Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
            String manufacturerName = resultSet.getString("name");
            String manufacturerCountry = resultSet.getString("country");
            Manufacturer manufacturer = new Manufacturer(manufacturerName, manufacturerCountry);
            Car car = new Car(carModel, manufacturer);
            car.setId(carId);
            return car;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't get car with manufacturer", throwable);
        }
    }

    private List<Driver> findDriversByCarId(Long id) {
        String getDrivers = "SELECT * FROM drivers "
                + "JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE car_id = ? AND deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement getAllDriversStatement
                             = connection.prepareStatement(getDrivers)) {
            getAllDriversStatement.setLong(1, id);
            List<Driver> drivers = new ArrayList<>();
            ResultSet resultSet = getAllDriversStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(parseDriver(resultSet));
            }
            return drivers;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Couldn't get a list of drivers from DB for car id "
                    + id, throwable);
        }
    }

    private Driver parseDriver(ResultSet resultSet) {
        Long newId = null;
        try {
            newId = resultSet.getObject("id", Long.class);
            String name = resultSet.getString("name");
            String licenseNumber = resultSet.getString("license_number");
            Driver driver = new Driver(name, licenseNumber);
            driver.setId(newId);
            return driver;
        } catch (SQLException throwable) {
            throw new DataProcessingException("Can't parse driver.", throwable);
        }
    }
}
