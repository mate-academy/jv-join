package mate.jdbc.service;

import java.util.List;
import java.util.stream.Collectors;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car crate(Car car) {
        return carDao.crate(car);
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
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Car car, Driver driver) {
        List<Driver> drivers = car.getDrivers();
        drivers.add(driver);
        car.setDrivers(drivers);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Car car, Driver driver) {
        List<Driver> drivers = car.getDrivers();
        drivers.remove(driver);
        car.setDrivers(drivers);
        carDao.update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> all = carDao.getAll();
        return all.stream()
                .filter(c -> c.getId() == driverId)
                .collect(Collectors.toList());
    }
}
