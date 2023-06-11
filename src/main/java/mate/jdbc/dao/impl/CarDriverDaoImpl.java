package mate.jdbc.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDriverDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.CarDriver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDriverDaoImpl implements CarDriverDao {
    @Override
    public CarDriver create(CarDriver carDriver) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, carDriver.getCarId());
            statement.setLong(2, carDriver.getDriverId());
            statement.executeUpdate();
            return carDriver;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't create carDriver in cars_drivers DB "
                    + carDriver + ". ", e);
        }
    }

    @Override
    public boolean delete(CarDriver carDriver) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE car_id = ? AND driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(query)) {
            statement.setLong(1, carDriver.getCarId());
            statement.setLong(2, carDriver.getDriverId());
            return statement.execute();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete carDriver "
                    + carDriver + " from cars_drivers DB.", e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String query = "SELECT cars.id, cars.model, cars.manufacturer_id FROM cars "
                + "JOIN cars_drivers ON cars.id = cars_drivers.car_id "
                + "WHERE cars.is_deleted = FALSE AND cars_drivers.driver_id = ?;";
        List<Car> cars = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of cars by driver id: "
                    + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getObject("id", Long.class);
        String model = resultSet.getString("model");
        Long manufacturerId = resultSet.getObject("manufacturer_id", Long.class);
        return new Car(id, model, manufacturerId);
    }
}
