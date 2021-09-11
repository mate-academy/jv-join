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
        String insertDriverRequest = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverStatement =
                        connection.prepareStatement(insertDriverRequest)) {
            insertDriverStatement.setLong(1, car.getId());
            insertDriverStatement.setLong(2, driver.getId());
            insertDriverStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert driver to car " + car, e);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String removeDriverFromCarRequest =
                "DELETE FROM cars_drivers WHERE car_id = ? AND driver_id = ?;";

        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriverFromCarStatement =
                        connection.prepareStatement(removeDriverFromCarRequest)) {
            removeDriverFromCarStatement.setLong(1, car.getId());
            removeDriverFromCarStatement.setLong(2, driver.getId());
            removeDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't remove driver " + driver + " from car " + car, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
