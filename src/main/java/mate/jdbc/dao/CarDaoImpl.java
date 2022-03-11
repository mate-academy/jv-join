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
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        String insertCarRequest = "insert into cars (manufacturer_id, model) value (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement insertCarStatement =
                     connection.prepareStatement(insertCarRequest, Statement.RETURN_GENERATED_KEYS)) {
            insertCarStatement.setLong(1,car.getManufacturer().getId());
            insertCarStatement.setString(2,car.getModel());
            insertCarStatement.executeUpdate();
            ResultSet getGeneratedKey = insertCarStatement.getGeneratedKeys();
            if (getGeneratedKey.next()) {
                Long id = getGeneratedKey.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create car: " + car, e);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String selectCarWithoutDriversRequest = "select c.id as car_id, "
                + "m.id as manufacturer_id, "
                + "m.name as manufacturer_name, "
                + "m.country as manufacturer_country, "
                + "c.model as car_model "
                + "from cars c "
                + "join manufacturers m on c.manufacturer_id = m.id "
                + "where c.id = ? and c.is_deleted = false;";
        Car car = new Car();
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarWithoutDriverStatement =
                     connection.prepareStatement(selectCarWithoutDriversRequest)) {
            getCarWithoutDriverStatement.setLong(1,id);
            ResultSet resultSet = getCarWithoutDriverStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB with id = " + id, e);
        }
        if (car != null) {
            car.setDrivers(getDriversForCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String selectCarWithoutDriversRequest = "select c.id as car_id, "
                + "m.id as manufacturer_id, "
                + "m.name as manufacturer_name, "
                + "m.country as manufacturer_country, "
                + "c.model as car_model "
                + "from cars c "
                + "join manufacturers m on c.manufacturer_id = m.id "
                + "c.is_deleted = false;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
             Statement getCarWithoutDriverStatement =
                     connection.createStatement()) {
            ResultSet resultSet = getCarWithoutDriverStatement.executeQuery(selectCarWithoutDriversRequest);
            while (resultSet.next()) {
                cars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car from DB", e);
        }
        if (!cars.isEmpty()) {
            cars.forEach(c -> c.setDrivers(getDriversForCar(c.getId())));
        }
        return cars;
    }

    @Override
    public Car update(Car car) {
        String updateRequest = "update cars set "
                + "manufacturer_id = ?, "
                + "model = ? "
                + "where id = ? and is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateRequest)) {
            statement.setLong(1,car.getManufacturer().getId());
            statement.setString(2, car.getModel());
            statement.setLong(3,car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car with id = " + car.getId(), e);
        }
        deleteCarDriverRelations(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "update cars set is_deleted = true where id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car with id " + id, e);
        }
    }

    private boolean deleteCarDriverRelations(Long carId) {
        String deleteRequest = "delete from cars_drivers where car_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteRequest)) {
            statement.setLong(1, carId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete relations of car with id " + carId, e);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("car_model"));
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("manufacturer_name"));
        manufacturer.setCountry(resultSet.getString("manufacturer_country"));
        car.setManufacturer(manufacturer);
        return car;
    }

    private List<Driver> getDriversForCar(Long driverId) {
        String selectDriversRequest = "select id, name, license_number"
                + "from drivers d"
                + "join cars_drivers cd on d.id = cd.driver_id"
                + "where cd.car_id = ? and is_deleted = false;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement getCarDriversStatement =
                     connection.prepareStatement(selectDriversRequest)) {
            getCarDriversStatement.setLong(1, driverId);
            ResultSet resultSet = getCarDriversStatement.executeQuery();
            List<Driver> drivers = new ArrayList<>();
            while (resultSet.next()) {
                drivers.add(parseDriverFromResultSet(resultSet));
            }
            return drivers;
        } catch (SQLException e) {
                throw new DataProcessingException("Can't get driver from DB with id = " + driverId, e);
        }
    }

    private Driver parseDriverFromResultSet(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setId(resultSet.getObject("id", Long.class));
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        return driver;
    }

    private void insertDrivers(Car car) {
        String insertDriversRequest = "insert into cars_drivers (driver_id, car_id) value (?,?);";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement insertDriversStatement =
                     connection.prepareStatement(insertDriversRequest)) {
            insertDriversStatement.setLong(2, car.getId());
            for (Driver driver: car.getDrivers()) {
                insertDriversStatement.setLong(1, driver.getId());
                insertDriversStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to DB.", e);
        }
    }
}
