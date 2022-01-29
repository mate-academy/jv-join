package mate.jdbc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import mate.jdbc.dao.CarDao;
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
        String insertDriversQuery =
                   "INSERT INTO cars_drivers (car_id,driver_id) VALUES (?,?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriverToCarStatement =
                        connection.prepareStatement(insertDriversQuery)) {
            addDriverToCarStatement.setLong(1,car.getId());
            for (Driver driverAdded:car.getDrivers()) {
                addDriverToCarStatement.setLong(2,driverAdded.getId());
                addDriverToCarStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert drivers to car " + driver, e);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String insertRemoveQuery =
                   "DELETE FROM cars_drivers WHERE car_id = ? and driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriverCarCoupleStatement =
                        connection.prepareStatement(insertRemoveQuery)) {
            removeDriverCarCoupleStatement.setLong(1,car.getId());
            removeDriverCarCoupleStatement.setLong(2,driver.getId());
            removeDriverCarCoupleStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete car_driver by car_id "
                    + car + " driver_id " + driver, e);
        }
    }
}
