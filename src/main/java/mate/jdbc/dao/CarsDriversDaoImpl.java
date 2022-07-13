package mate.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Dao;
import mate.jdbc.util.ConnectionUtil;

@Dao
public class CarsDriversDaoImpl implements CarsDriversDao {

    @Override
    public boolean pairCarDriver(Long carId, Long driverId) {
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.setLong(2, driverId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't pair car:" + carId + " driver with id "
                    + driverId, e);
        }
    }

    @Override
    public boolean unpairCarDriver(Long carId, Long driverId) {
        String query = "DELETE FROM cars_drivers WHERE car_id = ? AND driver_id = ? ;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, carId);
            statement.setLong(2, driverId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't unpair car:" + carId + " driver with id "
                    + driverId, e);
        }
    }
}
