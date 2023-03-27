package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
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
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(
                () -> new NoSuchElementException("No car found by id " + id));
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
    public List<Driver> getDriversForCar(Long carId) {
        return carDao.getDriversForCar(carId);
    }

    @Override
    public List<Car> getCarsForDriver(Long driverId) {
        return carDao.getCarsForDriver(driverId);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        if (car.getDrivers()
                .stream()
                .filter(d -> d.getId().equals(driver.getId()))
                .toArray().length > 0) {
            throw new DataProcessingException("Car already has driver with this driverId - "
                    + driver.getId() + ", " + car);
        }
        car.getDrivers().add(driver);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (car.getDrivers()
                .stream()
                .filter(d -> d.getId().equals(driver.getId()))
                .toArray().length == 0) {
            throw new DataProcessingException("There is no such driver with driverId - "
                    + driver.getId() + " in the car " + car);
        }
        car.getDrivers().remove(driver);
        carDao.update(car);
    }
}
