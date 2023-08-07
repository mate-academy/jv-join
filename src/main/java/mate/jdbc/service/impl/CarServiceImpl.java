package mate.jdbc.service.impl;

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
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public Car update(Car car) {
        if (carDao.update(car)) {
            return car;
        } else {
            throw new RuntimeException("Can't update car " + car);
        }
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(
                () -> new RuntimeException("Can't get car by id: " + id));
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
    public void addDriverToCar(Driver driver, Car car) {
        List<Driver> carDriverList = car.getDrivers();
        if (!carDriverList.contains(driver)) {
            carDriverList.add(driver);
        }
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> carDriverList = car.getDrivers();
        carDriverList.remove(driver);
        carDao.update(car);
    }
}
