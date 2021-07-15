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
    public Car getCarById(Long id) {
        return carDao.get(id).get();
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        car.getDrivers().add(driver);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        if (drivers.remove(driver)) {
            car.setDrivers(drivers);
            carDao.update(car);
        }
    }

    @Override
    public List<Car> getAllCars() {
        return carDao.getAll();
    }

    @Override
    public Car createCar(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car updateCar(Car car) {
        return carDao.update(car);
    }
}
