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
            throw new DataProcessingException("Couldn't create " + car + ". ", e);
        }
    }

    @Override
    public Optional<Car> get(Long id) {
        String query = "SELECT C.id, C.model, "
                + "M.id, M.name, M.country, "
                + "D.id, D.name, D.license_number "
                + "FROM cars C "
                + "INNER JOIN manufacturers M ON C.manufacturer_id = M.id "
                + "INNER JOIN cars_drivers CD ON C.id = CD.car_id "
                + "INNER JOIN drivers D ON CD.driver_id = D.id "
                + "WHERE C.is_deleted = FALSE AND M.is_deleted = FALSE "
                + "AND D.is_deleted = FALSE AND C.id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getStatement = connection.prepareStatement(query)) {
            getStatement.setLong(1, id);
            ResultSet result = getStatement.executeQuery();
            Optional<Car> optionalCar = Optional.empty();
            List<Driver> driversList = new ArrayList<>();
            if (result.next()) {
                Manufacturer manufacturer =
                        new Manufacturer(result.getObject("M.id", Long.class),
                                         result.getString("M.name"),
                                         result.getString("M.country"));
                driversList.add(new Driver(result.getObject("D.id", Long.class),
                                           result.getString("D.name"),
                                           result.getString("D.license_number")));
                optionalCar =
                        Optional.of(new Car(result.getObject("C.id", Long.class),
                                            result.getString("C.model"),
                                            manufacturer, driversList));
                while (result.next()) {
                    optionalCar.get().getDrivers()
                            .add(new Driver(result.getObject("D.id", Long.class),
                                            result.getString("D.name"),
                                            result.getString("D.license_number")));
                }
            }
            return optionalCar;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get car for id: " + id + ". ", e);
        }
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public Car delete(Long id) {
        return null;
    }
}
