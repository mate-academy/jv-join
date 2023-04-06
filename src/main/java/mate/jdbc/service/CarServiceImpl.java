package mate.jdbc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
    private CarDao cardao;

    @Override
    public Car create(Car car) {
        return cardao.create(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        return cardao.get(id);
    }

    @Override
    public List<Car> getAll() {
        return cardao.getAll();
    }

    @Override
    public Car update(Car car) {
        return cardao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return cardao.delete(id);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return cardao.getAllByDriver(driverId);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        String insertDriverToCarQuery = "INSERT INTO cars_drivers "
                + "(car_id, driver_id) VALUES(?, ?);";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement insertDriverToCarStatement
                        = connection.prepareStatement(insertDriverToCarQuery)) {
            insertDriverToCarStatement.setLong(1, car.getId());
            insertDriverToCarStatement.setLong(2, driver.getId());
            insertDriverToCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add driver: "
                    + driver + " car: " + car, e);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        String removeDriverFromCarQuery = "DELETE FROM cars_drivers "
                + "WHERE car_id = ? AND driver_id = ?;";
        try (Connection connection = ConnectionUtil.getConnection();
                PreparedStatement removeDriverFromCarStatement
                        = connection.prepareStatement(removeDriverFromCarQuery)) {
            removeDriverFromCarStatement.setLong(1, car.getId());
            removeDriverFromCarStatement.setLong(2, driver.getId());
            removeDriverFromCarStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataProcessingException("Can't remove driver: "
                    + driver + " from car: " + car, e);
        }
    }
}
