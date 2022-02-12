package mate.jdbc.service;

import java.util.List;
import java.util.Optional;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;
    @Inject
    private DriverDao driverDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Optional<Car> get(Long id) {
        return Optional.ofNullable(carDao.get(id));
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
    public boolean delete(Car id) {
        return carDao.delete(id);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        driverDao.create(driver);
        drivers.add(driver);
        car.setDrivers(drivers);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        drivers.remove(driver);
        car.setDrivers(drivers);
        carDao.update(car);
    }
}

