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
        String query = "insert into cars_drivers (car_id, driver_id) values (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.setLong(2, driver.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can not dd driver to the car! ", e);
        }
        car.getDrivers().add(driver);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String query = "delete from cars_drivers where car_id = ? and driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, car.getId());
            statement.setLong(2, driver.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can not remove car's drivers! ", e);
        }
        car.getDrivers().remove(driver);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
