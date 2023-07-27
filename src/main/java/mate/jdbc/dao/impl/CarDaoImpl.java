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
        String createCarRequest = "INSERT INTO cars (model, manufacturer_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection.prepareStatement(createCarRequest,
                        Statement.RETURN_GENERATED_KEYS)) {
            carStatement.setString(1, car.getModel());
            carStatement.setLong(2, car.getManufacturer().getId());
            carStatement.executeUpdate();
            ResultSet resultSet = carStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car: "
                    + car + ". ", e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectCarByIdRequest = "SELECT c.id as car_id, model, c.manufacturer_id,"
                + " m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.id = ? AND c.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection
                        .prepareStatement(selectCarByIdRequest)) {
            carStatement.setLong(1, id);
            ResultSet resultSet = carStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by ID " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectAllCarAddManufacturerRequest = "SELECT c.id as car_id, model, "
                + "c.manufacturer_id, m.name, m.country FROM cars c JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id WHERE c.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection
                        .prepareStatement(selectAllCarAddManufacturerRequest)) {
            ResultSet resultSet = carStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars from cars DB.", e);
        }
        selectAllCarAddDrivers(cars);
        return cars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars "
                + "SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE;";
        List<Driver> updateDriversList = car.getDrivers();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement
                        = connection.prepareStatement(query)) {
            carStatement.setString(1, car.getModel());
            carStatement.setLong(2, car.getManufacturer().getId());
            carStatement.setLong(3, car.getId());
            carStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update "
                    + car + " in cars DB.", e);
        }
        removeDriversFromCar(car);
        if (updateDriversList != null) {
            car.setDrivers(updateDriversList);
            addDriversToCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteCarByIdRequest = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection
                        .prepareStatement(deleteCarByIdRequest)) {
            carStatement.setLong(1, id);
            return carStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with ID " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getAllByDriverRequest = "SELECT cd.cars_id, cd.drivers_id, c.id as car_id,"
                + " c.manufacturer_id, model, m.name, m.country FROM cars_drivers cd "
                + "JOIN cars c ON cd.cars_id= c.id "
                + "JOIN manufacturers m ON c.manufacturer_id = m.id WHERE cd.drivers_id = ?";
        List<Car> carListForDriverById = new ArrayList<>();
        Car car;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection
                        .prepareStatement(getAllByDriverRequest)) {
            carStatement.setLong(1, driverId);
            ResultSet resultSet = carStatement.executeQuery();
            while (resultSet.next()) {
                car = getCar(resultSet);
                carListForDriverById.add(car);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get all cars for driver by ID "
                    + driverId, e);
        }
        return carListForDriverById;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("car_id", Long.class);
        String model = resultSet.getString("model");
        return new Car(id, model, addManufacturerForCar(resultSet));
    }

    private Manufacturer addManufacturerForCar(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private List<Driver> getDriversForCar(Long carId) {
        String getAllDrivesForCarRequest = "SELECT d.id, name, license_number FROM drivers d "
                + "JOIN cars_drivers cd ON d.id= cd.drivers_id WHERE cd.cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(getAllDrivesForCarRequest)) {
            statement.setLong(1, carId);
            ResultSet resultSet = statement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get driver by ID " + carId, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        return new Driver(id, name, licenseNumber);
    }

    private void addDriversToCar(Car car) {
        String addDriversToCarRequest = "INSERT INTO cars_drivers (cars_id, drivers_id)"
                + " VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection
                        .prepareStatement(addDriversToCarRequest,
                             Statement.RETURN_GENERATED_KEYS)) {
            carStatement.setLong(1, car.getId());
            List<Long> carsDriversId = new ArrayList<>();
            if (car.getDrivers() == null) {
                car.setDrivers(new ArrayList<>());
            }
            for (Driver current : car.getDrivers()) {
                carsDriversId.add(current.getId());
            }
            for (Long current : carsDriversId) {
                carStatement.setLong(2, current);
                carStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add drivers to car ID "
                    + car + ". ", e);
        }
    }

    private void removeDriversFromCar(Car car) {
        String deleteDriverFromCarRequest = "DELETE from cars_drivers "
                + "WHERE cars_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement carStatement = connection
                        .prepareStatement(deleteDriverFromCarRequest)) {
            carStatement.setLong(1, car.getId());
            carStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete drivers "
                    + "from car with ID " + car.getId(), e);
        }
    }

    private void selectAllCarAddDrivers(List<Car> cars) {
        String selectAllCarAddDriversRequest = "SELECT cd.cars_id, d.id, d.name, "
                + "d.license_number FROM cars_drivers cd "
                + "JOIN drivers d ON cd.drivers_id = d.id;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement driversStatement = connection
                        .prepareStatement(selectAllCarAddDriversRequest)) {
            ResultSet resultSet = driversStatement.executeQuery();
            while (resultSet.next()) {
                Long carId = resultSet.getLong("cars_id");
                Driver driver = getDriver(resultSet);
                for (Car car : cars) {
                    if (car.getId().equals(carId)) {
                        if (car.getDrivers() == null) {
                            car.setDrivers(new ArrayList<>());
                        }
                        car.getDrivers().add(driver);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get drivers for cars from cars DB.", e);
        }
    }
}
