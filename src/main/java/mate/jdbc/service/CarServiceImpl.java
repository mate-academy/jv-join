package mate.jdbc.service;

import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    CarDao carDao;

    @Override
    public Car create(final Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(final Long id) {
        return carDao.get(id).orElseThrow(
                () -> new RuntimeException("Could get Car by id: " + id + ". Value is null")
        );
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(final Car car) {
        return carDao.update(car);
    }

    @Override
    public boolean delete(final Long id) {
        return carDao.delete(id);
    }

    @Override
    public List<Car> getAllByDriver(final Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    @Override
    public void addDriverToCar(final Driver driver, final Car car) {
        if (car.getDrivers() == null) {
            List<Driver> drivers = new ArrayList<>();
            drivers.add(driver);
            car.setDrivers(drivers);
            update(car);
        } else {
            final List<Driver> drivers = car.getDrivers();
            if (!drivers.contains(driver)) {
                drivers.add(driver);
                update(car);
            }
        }
    }

    @Override
    public void removeDriverFromCar(final Driver driver, final Car car) {
        if (car.getDrivers() != null) {
            car.getDrivers().remove(driver);
            update(car);
        }
    }
}
