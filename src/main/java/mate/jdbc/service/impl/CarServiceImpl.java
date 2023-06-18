package mate.jdbc.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.util.ConnectionUtil;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() -> new NoSuchElementException("Could not get car "
                + "by id = " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(Car car) {
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?)";
        if (!isDriverRelateCar(driver.getId(), car.getId())) {
            try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement statement
                             = connection.prepareStatement(query)) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataProcessingException("Couldn't add relation driver "
                        + "with car in cars_driversDB.", e);
            }
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String query = "DELETE FROM cars_drivers WHERE driver_id = ? AND car_id = ?";
        if (!isDriverRelateCar(driver.getId(), car.getId())) {
            try (Connection connection = ConnectionUtil.getConnection();
                     PreparedStatement statement
                             = connection.prepareStatement(query)) {
                statement.setLong(1, driver.getId());
                statement.setLong(2, car.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataProcessingException("Couldn't delete relation "
                        + "driver with car in cars_driversDB.", e);
            }
        }

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    private boolean isDriverRelateCar(Long driverId, Long carId) {
        String query = "SELECT COUNT(*) FROM cars_drivers WHERE driver_id = ? AND car_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement
                         = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            statement.setLong(2, carId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't check relation "
                    + "driver with car in cars_driversDB.", e);
        }
        return false;
    }
}
