package mate.jdbc.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.CarDao;
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
    public Car create(Car element) {
        return carDao.create(element);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(
                () -> new NoSuchElementException("Could not get driver by id = " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(Car element) {
        return carDao.update(element);
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String insertDriversQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement addDriversStatement
                        = connection.prepareStatement(insertDriversQuery)) {
            addDriversStatement.setLong(1, car.getId());
            addDriversStatement.setLong(2, driver.getId());
            addDriversStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can`t insert drivers to car: "
                    + car.getId() + " driverId, " + driver.getId(), e);

        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String deleteRelationsQuery = "DELETE FROM cars_drivers cd WHERE cd.driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement
                        = connection.prepareStatement(deleteRelationsQuery)) {
            for (Driver driverDel : car.getDrivers()) {
                if (driver.equals(driverDel)) {
                    statement.setLong(1, driver.getId());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can`t relation cars_drivers carId:" + car.getId()
                    + ", driverId; " + driver.getId(), e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
