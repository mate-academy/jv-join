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
        String createCarQuery = "INSERT INTO cars (`model`,`manufacturer_id`) values (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                    PreparedStatement statement = connection
                        .prepareStatement(createCarQuery, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, car.getModel());
            statement.setObject(2, car.getManufacturer().getId());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                Long id = resultSet.getObject(1, Long.class);
                car.setId(id);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't create new car " + car, e);
        }
        addDriversToCar(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        Car car = null;
        String getCarByIdQuery =
                "SELECT cars.id AS car_id, model, manufacturer_id, name, country "
                        + "FROM cars "
                        + "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.is_deleted = FALSE AND cars.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement statement = connection
                        .prepareStatement(getCarByIdQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                car = getCar(resultSet);
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get car by id - " + id
                    + " from cars", e);
        }
        if (car != null) {
            car.setDrivers(getDriversByCar(id));
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        List<Car> carList = new ArrayList<>();
        String getAllCarsQuery =
                "SELECT cars.id AS car_id, model, manufacturer_id, name, country "
                        + "FROM cars "
                        + "INNER JOIN manufacturers ON cars.manufacturer_id = manufacturers.id "
                        + "WHERE cars.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                        Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(getAllCarsQuery);
            while (resultSet.next()) {
                carList.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get all cars", e);
        }
        for (Car car : carList) {
            car.setDrivers(getDriversByCar(car.getId()));
        }
        return carList;
    }

    @Override
    public Car update(Car car) {
        String updateCarQuery = "UPDATE cars SET model = ?, manufacturer_id = ? "
                + "WHERE id = ? AND is_deleted = FALSE";
        int numberOfChanges = 0;
        try (Connection connection = ConnectionUtil.getConnection();
                        PreparedStatement statement =
                        connection.prepareStatement(updateCarQuery)) {
            statement.setString(1, car.getModel());
            statement.setLong(2, car.getManufacturer().getId());
            statement.setLong(3, car.getId());
            numberOfChanges = statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't update car by id - " + car.getId(), e);
        }
        if (numberOfChanges != 0) {
            deleteDriversByCar(car);
            addDriversToCar(car);
        }
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String deleteQuery = "UPDATE cars SET is_deleted = TRUE WHERE id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(deleteQuery)) {
            statement.setLong(1, id);
            int numberOfDeletedRows = statement.executeUpdate();
            return numberOfDeletedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete car by id = " + id, e);
        }
    }

    private boolean deleteDriversByCar(Car car) {
        String deleteAllDriversQuery = "DELETE FROM drivers_cars WHERE car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(deleteAllDriversQuery)) {
            statement.setLong(1, car.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException(
                    "Can't deleted drivers by car " + car, e);
        }
    }

    private Car addDriversToCar(Car car) {
        String insertDriversQuery =
                "INSERT INTO drivers_cars (car_id,driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(insertDriversQuery)) {
            statement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                statement.setLong(2, driver.getId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't insert drivers to car " + car, e);
        }
        return car;
    }

    private List<Car> getCarsByDriver(Long id) {
        List<Car> cars = new ArrayList<>();
        String getAllCarsByDriverQuery =
                "SELECT joined_table.car_id, joined_table.model, manufacturer_id, "
                        + "manufacturers.name, manufacturers.country "
                        + "FROM manufacturers "
                        + "INNER JOIN ("
                        + "SELECT * FROM cars "
                        + "INNER JOIN drivers_cars ON cars.id = drivers_cars.car_id"
                        + ") as joined_table ON manufacturers.id = joined_table.car_id;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement(getAllCarsByDriverQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get cars by driver id - " + id, e);
        }
        for (Car car : cars) {
            car.setDrivers(getDriversByCar(car.getId()));
        }
        return cars;
    }

    private List<Driver> getDriversByCar(Long id) {
        List<Driver> driversList = new ArrayList<>();
        String getManufacturerForCarQuery =
                "SELECT * FROM drivers_cars "
                        + "JOIN drivers ON drivers_cars.driver_id = drivers.id "
                        + "WHERE drivers_cars.car_id = ? AND drivers.is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(getManufacturerForCarQuery)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                driversList.add(getDriver(resultSet));
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't get drivers for car by id " + id, e);
        }
        return driversList;
    }

    private Driver getDriver(ResultSet resultSet) throws SQLException {
        Driver driver = new Driver();
        driver.setName(resultSet.getString("name"));
        driver.setLicenseNumber(resultSet.getString("license_number"));
        driver.setId(resultSet.getObject("id", Long.class));
        return driver;
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("car_id", Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id", Long.class));
        manufacturer.setName(resultSet.getString("name"));
        manufacturer.setCountry(resultSet.getString("country"));
        car.setManufacturer(manufacturer);
        return car;
    }
}
