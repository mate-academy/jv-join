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
                () -> new NoSuchElementException("Can`t find car with id: " + id));
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
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        if (car.getDrivers().contains(driver)) {
            throw new DataProcessingException("Driver "
                    + driver.getName()
                    + " has been already added to car "
                    + car.getModel());
        }
        car.getDrivers().add(driver);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (car.getDrivers().remove(driver)) {
            carDao.update(car);
        } else {
            throw new DataProcessingException("Can't delete Driver "
                    + driver.getName()
                    + " from Car!"
                    + car.getModel());
        }
    }
}
