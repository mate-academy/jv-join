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
        String insertQuery = "INSERT INTO cars(model, manufacturer_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery,
                     Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                car.setId(resultSet.getObject(1, Long.class));
            }
        } catch (SQLException exp) {
            throw new DataProcessingException("Can't create "
                    + car + "on DB", exp);
        }
        insertDrivers(car);
        return car;
    }

    @Override
    public Optional<Car> get(Long id) {
        String query =
                        "SELECT c.id id, c.model model, m.id manufacturer_id, m.name manufacturer," +
                                " m.country country" +
                        "FROM cars c" +
                        "INNER JOIN manufacturers m" +
                        "ON c.manufacturer_id = m.id " +
                        "WHERE c.id = ? AND c.is_deleted = FALSE";
        Car car = null;
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                car = parseCarFromResultSet(resultSet);
            }
        } catch (SQLException exc) {
            throw new DataProcessingException("Can't get car by id "
                    + id, exc);
        }
        if(car != null) {
            setCarDrivers(car);
        }
        return Optional.ofNullable(car);
    }

    @Override
    public List<Car> getAll() {
        String query =
                        "SELECT c.id id, c.model model, m.id manufacturer_id, m.name manufacturer," +
                                " m.country country" +
                        "FROM cars c " +
                        "INNER JOIN manufactures m " +
                        "ON c.manufacturer_id = m.id" +
                        "WHERE c.is_deleted = FALSE";
        List<Car> listOfCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
         ResultSet resultSet = preparedStatement.executeQuery();
         while (resultSet.next()) {
             Car car = parseCarFromResultSet(resultSet);
             listOfCars.add(car);
         }
        } catch (SQLException exc) {
            throw new DataProcessingException(
                    "Can't get all entries from table Cars", exc);
        }
        return listOfCars;
    }

    @Override
    public Car update(Car car) {
        String query = "UPDATE cars SET model = ?, manufacturers_id = ?"
                + " WHERE id = ? AND is_deleted = FALSE;";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {
            preparedStatement.setString(1, car.getModel());
            preparedStatement.setLong(2, car.getManufacturer().getId());
            preparedStatement.setLong(3, car.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can`t update car from table Cars" + car, e);
        }
        deleteCarDrivers(car.getId());
        insertDrivers(car);
        return car;
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try(Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
        } catch (SQLException exc) {
            throw new DataProcessingException("Couldn't delete car by id = " + id, exc);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "" +
                "SELECT c.id id, c.model model," +
                " m.id manufacturer_id, m.name manufacturer, " +
                "m.country country" +
                "FROM cars c " +
                "INNER JOIN cars_drivers c_d " +
                "ON cars.id = cars_drivers.car_id " +
                "INNER JOIN manufacturers m" +
                "ON manufacturers.id = cars.manufacturer_id" +
                "WHERE driver_id = ? AND cars.is_deleted = FALSE " +
                "AND drivers.is_deleted = FALSE";
        List<Car> listOfCars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, driverId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listOfCars.add(parseCarFromResultSet(resultSet));
            }
        } catch (SQLException exc) {
            throw new DataProcessingException("Couldn't get all cars by driver id" + driverId, exc);
        }
    }

    private Car parseCarFromResultSet(ResultSet resultSet) throws SQLException {
        Car car = new Car();
        car.setId(resultSet.getObject("id",Long.class));
        car.setModel(resultSet.getString("model"));
        Manufacturer manufacturer = parseManufacturerFromResultSet(resultSet);
        car.setManufacturer(manufacturer);
        return car;
    }

    private Manufacturer parseManufacturerFromResultSet(ResultSet resultSet) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(resultSet.getObject("manufacturer_id");
        manufacturer.setName(resultSet.getString("manufacturer"));
        manufacturer.setCountry(resultSet.getString("country"));
        return manufacturer;
    }

    private void insertDrivers(Car car) {
        String insertQuery = "INSERT INTO cars_drivers(car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setLong(1, car.getId());
            for (Driver driver : car.getDrivers()) {
                preparedStatement.setLong(2, driver.getId());
                preparedStatement.execute();
            }
        } catch (SQLException exc) {
            throw new DataProcessingException("Couldn't insert drivers to "
                    + car + ". ", exc);
        }
    }

    private void setCarDrivers(Car car) {
        String query = "" +
                "SELECT id, name, license_number " +
                "FROM cars_drivers " +
                "INNER JOIN drivers " +
                "ON cars_drivers.driver_id = drivers.id" +
                "WHERE drivers.driver_id = ? AND drivers.is_deleted = FALSE";
        try(Connection connection = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setLong(1, car.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            for (resultSet.next()) {
                Driver driver = new Driver();
                driver.setId(resultSet.getObject(1,Long.class););
                driver.setName(resultSet.getString("name"));
                driver.setLicenseNumber(resultSet.getString("license_number"));
                car.getDrivers().add(driver);
            }
        } catch (SQLException exc) {
           throw new DataProcessingException("Couldn't get all drivers from " + car, exc);
        }
    }

    private void deleteCarDrivers(Long id) {
        String query = "DELETE FROM cars_models WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (SQLException exc) {
           throw new DataProcessingException("Can't delete entries about car drivers by ID =" + id, exc);
        }
    }
}
