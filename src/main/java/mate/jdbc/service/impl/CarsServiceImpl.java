package mate.jdbc.service.impl;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarsService;

@Service
public class CarsServiceImpl implements CarsService {
    @Inject
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() ->
                new RuntimeException("Can't get car with id: " + id));
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
    public void addDriverToCar(Driver driver, Car car) {
        Car carFromDb = get(car.getId());
        if (!carFromDb.getDrivers().contains(driver)) {
            carFromDb.getDrivers().add(driver);
            update(carFromDb);
        } else {
            throw new RuntimeException(driver + "already in drivers list for " + car);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        Car carFromDb = get(car.getId());
        if (carFromDb.getDrivers().remove(driver)) {
            update(carFromDb);
        } else {
            throw new RuntimeException(driver + " is not in the drivers list for " + car);
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
