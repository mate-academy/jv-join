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
        String query = "INSERT INTO cars (model, manufacturer_id) VALUES(?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement createStatement =
                        connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            createStatement.setString(1, car.getModel());
            createStatement.setLong(2, car.getManufacturer().getId());
            createStatement.executeUpdate();
            ResultSet result = createStatement.getGeneratedKeys();
            if (result.next()) {
                car.setId(result.getObject(1, Long.class));
            }
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create car " + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT c.id, c.model, "
                + "m.id, m.name, m.country, "
                + "d.id, d.name, d.license_number "
                + "FROM cars c "
                + "INNER JOIN manufacturers m ON C.manufacturer_id = m.id "
                + "INNER JOIN cars_drivers cd ON C.id = cd.car_id "
                + "INNER JOIN drivers d ON cd.driver_id = d.id "
                + "WHERE c.is_deleted = FALSE AND m.is_deleted = FALSE "
                + "AND d.is_deleted = FALSE AND c.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet result = getStatement.executeQuery();
            Optional<Car> optionalCar = Optional.empty();
            List<Driver> driversList = new ArrayList<>();
            if (result.next()) {
                Manufacturer manufacturer = getInstanceManufacture(result);
                driversList.add(getInstanceDriver(result));
                optionalCar =
                        Optional.of(new Car(result.getObject("c.id", Long.class),
                                            result.getString("c.model"),
                                            manufacturer, driversList));
                while (result.next()) {
                    optionalCar.get().getDrivers().add(getInstanceDriver(result));
                }
            }
            return optionalCar;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car for id: " + id + ". ", e);
        }
    }

    @Override
    public List<Car> getAll() {
        String query = "SELECT c.id, c.model, m.id, m.name, m.country "
                + "FROM cars c "
                + "INNER JOIN manufacturers m "
                + "ON c.manufacturer_id = m.id "
                + "WHERE c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllStatement = connection.prepareStatement(query)) {
            ResultSet result = getAllStatement.executeQuery();
            List<Car> carList = new ArrayList<>();
            while (result.next()) {
                Manufacturer manufacturer = getInstanceManufacture(result);
                carList.add(new Car(result.getObject("c.id", Long.class),
                                    result.getString("c.model"),
                                    manufacturer));
            }
            return carList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get list of cars" + ". ", e);
        }
    }

    @Override
    public Car update(Car car) {
        String queryDelete = "DELETE FROM cars_drivers WHERE car_id = ?";
        String queryInsert = "INSERT cars_drivers (driver_id, car_id) VALUES(?, ?)";
        String queryUpdateManufacturer =
                "UPDATE manufacturers SET name = ?, country = ? WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(queryDelete);
                PreparedStatement insertStatement = connection.prepareStatement(queryInsert);
                PreparedStatement updateManufacturerStatement =
                        connection.prepareStatement(queryUpdateManufacturer)) {
            deleteStatement.setLong(1, car.getId());
            deleteStatement.executeUpdate();
            insertStatement.setLong(2, car.getId());
            for (int i = 0; i < car.getDrivers().size(); i++) {
                insertStatement.setLong(1, car.getDrivers().get(i).getId());
                insertStatement.executeUpdate();
            }
            updateManufacturerStatement.setString(1, car.getManufacturer().getName());
            updateManufacturerStatement.setString(2, car.getManufacturer().getCountry());
            updateManufacturerStatement.setLong(3, car.getManufacturer().getId());
            updateManufacturerStatement.executeUpdate();
            return car;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't update car " + car + ". ", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String query = "UPDATE cars SET is_deleted = TRUE WHERE id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(query)) {
            deleteStatement.setLong(1, id);
            int x = deleteStatement.executeUpdate();
            return x > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete car by id" + id + ". ", e);
        }
    }

    private Manufacturer getInstanceManufacture(ResultSet result) throws SQLException {
        return new Manufacturer(result.getObject("m.id", Long.class),
                                result.getString("m.name"),
                                result.getString("m.country"));

    }

    private Driver getInstanceDriver(ResultSet result) throws SQLException {
        return new Driver(result.getObject("d.id", Long.class),
                          result.getString("d.name"),
                          result.getString("d.license_number"));
    }

    //    public void fillCarsDriversTable() {
    //    String query = "INSERT INTO cars_drivers (driver_id, car_id) "
    //                    + "VALUES (2, 1), (2, 2), (3, 3), (3, 4), (4, 5),"
    //                    + " (4, 1), (5, 2), (5, 3), (6, 4), (6, 5)";
    //            try (Connection connection = ConnectionUtil.getConnection();
    //                 PreparedStatement fillStatement = connection.prepareStatement(query)) {
    //                fillStatement.executeUpdate();
    //        } catch (SQLException e) {
    //            throw new RuntimeException("Couldn't fill cars_drivers table. ", e);
    //        }
    //    }
}
