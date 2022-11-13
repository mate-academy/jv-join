package mate.jdbc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
        return carDao.get(id).orElseThrow();
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
        String query = "INSERT INTO cars_drivers (driver_id, car_id) VALUES (?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driver.getId());
            statement.setLong(2, car.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("can not add driver to car. Params:"
                    + driver.toString()
                    + " " + car.toString());
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String query = "DELETE FROM cars_drivers "
                + "WHERE driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driver.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("can not remove driver from car. Params:"
                    + driver.toString()
                    + " " + car.toString(), e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = new ArrayList<>();
        String query = "SELECT drivers.name, drivers.license_number,"
                + " manufacturers.name as manufacturer, manufacturers.country, "
                + "cars.id, cars.model "
                + "FROM  cars_drivers "
                + "INNER JOIN cars on cars_drivers.car_id = cars.id "
                + "inner JOIN manufacturers on cars.manufacturer_id = manufacturers.id "
                + "INNER JOIN drivers on cars_drivers.driver_id = drivers.id "
                + "WHERE driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, driverId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Car car = new Car();
                car.setId(resultSet.getLong("id"));
                car.setModel(resultSet.getString("model"));
                car.setManufacturerId(get(car.getId()).getManufacturerId());
                car.setManufacturer(get(car.getId()).getManufacturer());
                cars.add(car);
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("can not get a list of cars of driver. "
                    + "Params: id=" + driverId, e);
        }
    }
}
