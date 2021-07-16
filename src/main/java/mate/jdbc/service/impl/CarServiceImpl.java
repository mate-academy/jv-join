package mate.jdbc.service.impl;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
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
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        List<Driver> addDriver = car.getDrivers();
        if (!addDriver.contains(driver)) {
            addDriver.add(driver);
            car.setDrivers(addDriver);
            carDao.update(car);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> removeDriver = new ArrayList<>(car.getDrivers());
        if (removeDriver.contains(driver)) {
            removeDriver.remove(driver);
            car.setDrivers(removeDriver);
            carDao.update(car);
        }
    }
}
