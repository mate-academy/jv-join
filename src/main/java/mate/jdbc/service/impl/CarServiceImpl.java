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
        List<Car> cars;
        cars = carDao.getAll();
        for (Car carItem : cars) {
            if (car.getModel().equals(carItem.getModel())) {
                return carItem;
            }
        }
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
        List<Car> cars;
        cars = carDao.getAll();
        for (Car carItem : cars) {
            if (car.getModel().equals(carItem.getModel())
                    && !carItem.getId().equals(car.getId())) {
                return carItem;
            }
        }
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        for (Driver driverForCar : drivers) {
            if (driverForCar.getLicenseNumber().equals(driver.getLicenseNumber())) {
                return;
            }
        }
        car.getDrivers().add(driver);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        for (Driver driverForCar : drivers) {
            if (driverForCar.getLicenseNumber().equals(driver.getLicenseNumber())) {
                car.getDrivers().remove(driverForCar);
                carDao.update(car);
            }
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
