package mate.jdbc.service;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

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
        if (!isDriverPresentInCar(driver, car)) {
            car.getDrivers().add(driver);
            carDao.update(car);
        } else {
            throw new DataProcessingException("Such driver already in car. " + driver + car);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (isDriverPresentInCar(driver, car)) {
            car.getDrivers().remove(driver);
            carDao.update(car);
        } else {
            throw new DataProcessingException("No such driver in car. " + driver + car);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    private boolean isDriverPresentInCar(Driver driver, Car car) {
        for (Driver dr: car.getDrivers()) {
            if (driver.equals(dr)) {
                return true;
            }
        }
        return false;
    }
}
