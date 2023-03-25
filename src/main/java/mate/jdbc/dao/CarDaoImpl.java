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
        String insertRequest = "INSERT INTO cars(model, manufacturers_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection
                        .prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS);) {
            insertStatement.setString(1, car.getModel());
            insertStatement.setLong(2, car.getManufacturer().getId());
            insertStatement.executeUpdate();
            ResultSet generatedKeys = insertStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert car: " + car + " to db", e);
        }
        insertDriver(car);
        return car;
    }

    private void insertDriver(Car car) {
        String insertDriverRequest = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertStatement = connection
                        .prepareStatement(insertDriverRequest);) {
            insertStatement.setLong(1, car.getId());
            List<Driver> drivers = car.getDrivers();
            for (Driver driver : drivers) {
                Long idDriver = driver.getId();
                insertStatement.setLong(2, idDriver);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert driver to car", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String getCarRequest = "SELECT cars.id, cars.manufacturers_id, cars.model, "
                + "manufacturers.name, manufacturers.country FROM cars "
                + "INNER JOIN manufacturers ON cars.manufacturers_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection
                        .prepareStatement(getCarRequest);) {
            getStatement.setLong(1, id);
            ResultSet resultSet = getStatement.executeQuery();
            if (resultSet.next()) {
                car = getCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get a Car by id: " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCarId(id));
        }
        return Optional.of(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = new ArrayList<>();
        String getAllCarsRequest = "SELECT * FROM cars INNER JOIN manufacturers "
                + "On cars.manufacturers_id = manufacturers.id WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection
                        .prepareStatement(getAllCarsRequest);) {
            ResultSet resultSet = getStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars from db", e);
        }
        for (Car car : cars) {
            if (cars != null) {
                car.setDrivers(getDriversByCarId(car.getId()));
            }
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "UPDATE cars SET model = ?, manufacturers_id = ? WHERE id = ? "
                + "AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateStatement = connection
                        .prepareStatement(updateRequest);) {
            updateStatement.setString(1, car.getModel());
            updateStatement.setLong(2, car.getManufacturer().getId());
            updateStatement.setLong(3, car.getId());
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car: " + car, e);
        }
        deleteRelations(car);
        addRelations(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteRequest = "UPDATE cars SET cars.is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection
                        .prepareStatement(deleteRequest);) {
            deleteStatement.setLong(1, id);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car with id: " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String getCarsByDriver = "SELECT * FROM cars INNER JOIN manufacturers "
                + "ON cars.manufacturers_id = manufacturers.id "
                + "INNER JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsByStatement = connection
                        .prepareStatement(getCarsByDriver);) {
            getCarsByStatement.setLong(1, driverId);
            ResultSet resultSet = getCarsByStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id: " + driverId, e);
        }
        for (Car car : cars) {
            if (car != null) {
                car.setDrivers(getDriversByCarId(car.getId()));
            }
        }
        return cars;
    }

    private Car getCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("cars.id", Long.class));
        car.setModel(resultSet.getString("cars.model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("cars.manufacturers_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturers.name"));
        manufacturer.setCountry(resultSet.getString("manufacturers.country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversByCarId(Long id) {
        List<Driver> drivers = new ArrayList<>();
        String driversByTheCarId = "SELECT * FROM drivers INNER JOIN cars_drivers "
                + "ON drivers.id = cars_drivers.driver_id WHERE cars_drivers.car_id = ? "
                + "AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriverStatement = connection
                        .prepareStatement(driversByTheCarId);) {
            getDriverStatement.setLong(1, id);
            ResultSet resultSet = getDriverStatement.executeQuery();
            while (resultSet.next()) {
                drivers.add(new Driver(
                        resultSet.getObject("driver_id", Long.class),
                        resultSet.getString("drivers.name"),
                        resultSet.getString("license_number")));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find the driver by the car id: " + id, e);
        }
        return drivers;
    }

    private void deleteRelations(Car car) {
        String deleteRequest = "DELETE FROM cars_drivers WHERE cars_drivers.car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDeleteStatement = connection
                        .prepareStatement(deleteRequest);) {
            getDeleteStatement.setLong(1, car.getId());
            getDeleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete relations!", e);
        }
    }

    private void addRelations(Car car) {
        String addRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAddStatement = connection
                        .prepareStatement(addRequest);) {
            getAddStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                getAddStatement.setLong(2, driver.getId());
                getAddStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add relations for car: " + car, e);
        }
    }
}
