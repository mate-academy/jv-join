package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String createCarRequest = "INSERT INTO cars (manufacturer_id, model) "
                + "VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createCarStatement = connection.prepareStatement(createCarRequest,
                        PreparedStatement.RETURN_GENERATED_KEYS)) {
            createCarStatement.setLong(1, car.getManufacturer().getId());
            createCarStatement.setString(2, car.getModel());
            createCarStatement.executeUpdate();
            ResultSet resultSet = createCarStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car. " + car, e);
        }
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectCarRequest = "SELECT * FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.id = ? AND cars.is_deleted = FALSE;";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement selectCarStatement =
                        connection.prepareStatement(selectCarRequest)) {
            selectCarStatement.setLong(1, id);
            ResultSet resultSet = selectCarStatement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car by id " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectAllCarsRequest = "SELECT * FROM cars "
                + "JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                + "WHERE cars.is_deleted = FALSE;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement selectAllCarsStatement =
                        connection.prepareStatement(selectAllCarsRequest)) {
            ResultSet resultSet = selectAllCarsStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars "
                    + "from cars table. ", e);
        }
        cars.forEach(car -> car.setDrivers(getDriversForCar(car.getId())));
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateCarRequest = "UPDATE cars SET manufacturer_id = ?, model = ?"
                + " WHERE id = ? AND is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement updateCarStatement =
                        connection.prepareStatement(updateCarRequest)) {
            updateCarStatement.setLong(1, car.getManufacturer().getId());
            updateCarStatement.setString(2, car.getModel());
            updateCarStatement.setLong(3, car.getId());
            updateCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update a car " + car, e);
        }
        deleteCarRelations(car.getId());
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
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete a car by id " + id, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long id) {
        String getCarsForDriverRequest = "SELECT cars.id, cars.manufacturer_id, cars.model "
                + "FROM cars JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars_drivers.driver_id = ? AND cars.is_deleted = FALSE";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getCarsForDriverStatement =
                        connection.prepareStatement(getCarsForDriverRequest)) {
            getCarsForDriverStatement.setLong(1, id);
            ResultSet resultSet = getCarsForDriverStatement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCarWithoutManufacturer(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars for driver with id " + id, e);
        }
        cars.forEach(car -> {
            car.setManufacturer(getManufacturerForCar(car.getId()));
            car.setDrivers(getDriversForCar(car.getId()));
        });
        return cars;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        String model = resultSet.getString("model");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        Car car = new Car(manufacturer, model);
        Long id = resultSet.getObject("id", Long.class);
        car.setId(id);
        return car;
    }

    private Car getCarWithoutManufacturer(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Car car = new Car();
        car.setId(id);
        car.setModel(model);
        return car;
    }

    private Manufacturer getManufacturerForCar(Long id) {
        String getManufacturerForCarRequest = "SELECT manufacturers.id, manufacturers.name, "
                + "manufacturers.country FROM manufacturers JOIN cars "
                + "ON manufacturers.id = cars.manufacturer_id "
                + "WHERE cars.id = ? AND manufacturers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getManufacturerForCarStatement =
                        connection.prepareStatement(getManufacturerForCarRequest)) {
            getManufacturerForCarStatement.setLong(1, id);
            ResultSet resultSet = getManufacturerForCarStatement.executeQuery();
            Manufacturer manufacturer = new Manufacturer();
            if (resultSet.next()) {
                manufacturer.setId(resultSet.getObject("id", Long.class));
                manufacturer.setName(resultSet.getString("name"));
                manufacturer.setCountry(resultSet.getString("country"));
            }
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get manufacturer for car with id"
                    + id, e);
        }
    }

    private List<Driver> getDriversForCar(Long id) {
        String getDriversForCarRequest = "SELECT drivers.id, drivers.name, drivers.license_number "
                + "FROM drivers JOIN cars_drivers ON drivers.id = cars_drivers.driver_id "
                + "WHERE cars_drivers.car_id = ? AND drivers.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getDriversForCarStatement =
                        connection.prepareStatement(getDriversForCarRequest)) {
            getDriversForCarStatement.setLong(1, id);
            ResultSet resultSet = getDriversForCarStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(getDriver(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car with id " + id, e);
        }
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String name = resultSet.getString("name");
        String licenseNumber = resultSet.getString("license_number");
        Driver driver = new Driver(name, licenseNumber);
        driver.setId(id);
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriversStatement =
                        connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                insertDriversStatement.setLong(2, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add drivers for car. " + car, e);
        }
    }

    private void deleteCarRelations(Long id) {
        String deleteRelationsRequest = "DELETE FROM cars_drivers WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteRelationsStatement =
                        connection.prepareStatement(deleteRelationsRequest)) {
            deleteRelationsStatement.setLong(1, id);
            deleteRelationsStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update data in cars_drivers table", e);
        }
    }
}
