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
import mate.jdbc.model.Manufacturer;
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
        return carDao.get(id).get();
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
        String insertQuery = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement setDriverToCarStatement =
                        connection.prepareStatement(insertQuery)) {
            setDriverToCarStatement.setLong(1, car.getId());
            setDriverToCarStatement.setLong(2, driver.getId());
            setDriverToCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver" + driver + " to car " + car, e);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String deleteQuery = "DELETE FROM cars_drivers WHERE car_id = ? and driver_id = ?";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement setDriverToCarStatement =
                        connection.prepareStatement(deleteQuery)) {
            setDriverToCarStatement.setLong(1, car.getId());
            setDriverToCarStatement.setLong(2, driver.getId());
            setDriverToCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete driver"
                    + driver + " from car " + car, e);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        String getRequest = "SELECT c.id, c.manufacturer_id, c.model, m.name, m.country "
                + "FROM cars c "
                + "JOIN manufacturers m ON m.id = c.manufacturer_id "
                + "JOIN cars_drivers cd ON c.id = cd.car_id "
                + "WHERE cd.driver_id = ? AND c.is_deleted = FALSE";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement getAllCarsStatement = connection.prepareStatement(getRequest)) {
            getAllCarsStatement.setLong(1, driverId);
            ResultSet resultSet = getAllCarsStatement.executeQuery();
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(getCar(resultSet));
            }
            return cars;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't find cars in DB by driver driverId "
                    + driverId, e);
        }
    }

    private Car getCar(ResultSet resultSet) throws SQLException {
        Long manufacturerId = resultSet.getLong("manufacturer_id");
        String manufacturerName = resultSet.getString("name");
        String manufacturerCountry = resultSet.getString("country");
        Manufacturer manufacturer =
                new Manufacturer(manufacturerId, manufacturerName, manufacturerCountry);
        Long id = resultSet.getLong("id");
        String model = resultSet.getString("model");
        return new Car(id, model, manufacturer);
    }
}
