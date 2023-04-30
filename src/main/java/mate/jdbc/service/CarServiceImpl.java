package mate.jdbc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
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
        return carDao.get(id);
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
        String insertDriverQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement =
                        connection.prepareStatement(insertDriverQuery)) {
            addDriverToCarStatement.setLong(1, car.getId());
            addDriverToCarStatement.setLong(2, driver.getId());
            addDriverToCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't add driver "
                    + driver + "to car " + car + ". ", e);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String removeDriverQuery = "UPDATE cars_drivers SET is_deleted = TRUE "
                + "WHERE car_id = ? AND driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriverFromCar =
                        connection.prepareStatement(removeDriverQuery)) {
            removeDriverFromCar.setLong(1, car.getId());
            removeDriverFromCar.setLong(2, driver.getId());
            removeDriverFromCar.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Couldn't remove driver " + driver
                    + " from car " + car, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
