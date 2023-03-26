package mate.jdbc.service.impl;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        if (car == null) {
            throw new DataProcessingException("Can't create car from null");
        }
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        if (id == null) {
            throw new DataProcessingException("Can't get car from null");
        }
        return carDao.get(id);
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        if (driverId == null) {
            throw new DataProcessingException("Can't get cars for driver null");
        }
        return carDao.getAllByDriver(driverId);
    }

    @Override
    public Car update(Car car) {
        if (car == null) {
            throw new DataProcessingException("Can't update info when car is null");
        }
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            throw new DataProcessingException("Can't delete car null");
        }
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        if (driver == null) {
            throw new DataProcessingException("Can't add driver with null value to the car " + car);
        }
        if (car == null) {
            throw new DataProcessingException("Can't add driver to the car " + car);
        }
        List<Driver> carDrivers = car.getDrivers();
        if (carDrivers.contains(driver)) {
            throw new DataProcessingException("Driver " + driver
                    + " already assigned to car " + car);
        }
        carDrivers.add(driver);
        update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (driver == null) {
            throw new DataProcessingException("Can't remove driver with null value, car = " + car);
        }
        if (car == null) {
            throw new DataProcessingException("Can't remove driver from the car " + car);
        }
        List<Driver> carDrivers = car.getDrivers();
        if (!carDrivers.contains(driver)) {
            throw new DataProcessingException("Driver " + driver
                    + " is not assigned to car " + car + ". Can't proceed removal");
        }
        carDrivers.remove(driver);
        update(car);
    }
}
