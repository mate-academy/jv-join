package mate.jdbc.service;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
public class CarServiceImpl implements CarService {
    private static final Logger logger = LogManager.getLogger(CarServiceImpl.class);

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
        logger.info("Method addDriverToCar was called with car " + car + " and driver " + driver);
        String query = "INSERT INTO cars_drivers (car_id, driver_id) VALUES (?, ?);";
        carDao.updateDriversForCar(query, driver, car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        logger.info("Method removeDriverFromCar was called with car "
                + car + " and driver " + driver);
        String query = "DELETE FROM cars_drivers WHERE car_id = ? AND driver_id = ?;";
        carDao.updateDriversForCar(query, driver, car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
