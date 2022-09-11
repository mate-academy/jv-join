package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.CarDriver;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarDriverDaoImpl implements CarDriverDao {
    @Override
    public void create(CarDriver carDriver) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carDriver.getCarId());
            statement.setLong(2, carDriver.getDriverId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver to car. CarId: "
                    + carDriver.getCarId() + "; driverId: " + carDriver.getDriverId() + ".", e);
        }
    }

    @Override
    public List<CarDriver> getByDriverId(Long driverId) {
        String query = "SELECT car_id "
                + "FROM cars_drivers "
                + "WHERE driver_id = ?";
        List<CarDriver> carDriverList = new ArrayList<>();
        try (Connection driverConnection = ConnectionUtil.getConnection();
                PreparedStatement statement = driverConnection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long carId = resultSet.getObject("car_id", Long.class);
                CarDriver carDriver = new CarDriver(carId, driverId);
                carDriverList.add(carDriver);
            }
            return carDriverList;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't get a list of drivers from DB.", e);
        }
    }

    @Override
    public void delete(CarDriver carDriver) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ? and driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carDriver.getCarId());
            statement.setLong(2, carDriver.getDriverId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't delete driver from car. Car: "
                    + carDriver.getCarId() + "; driverId: " + carDriver.getDriverId() + ".", e);
        }
    }
}
