package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
        Objects.requireNonNull(car, "car must not be null");
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return carDao.get(id).orElseThrow(() ->
                new NoSuchElementException("There is no car with id" + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    @Override
    public Car update(Car car) {
        Objects.requireNonNull(car, "car must not be null");
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        Objects.requireNonNull(car, "car must not be null");
        Objects.requireNonNull(driver, "driver must not be null");
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
        Objects.requireNonNull(car, "car must not be null");
        Objects.requireNonNull(driver, "driver must not be null");
        List<Driver> carDrivers = car.getDrivers();
        if (!carDrivers.contains(driver)) {
            throw new DataProcessingException("Driver " + driver
                    + " is not assigned to car " + car + ". Can't proceed removal");
        }
        carDrivers.remove(driver);
        update(car);
    }
}
