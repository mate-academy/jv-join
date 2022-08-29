package mate.jdbc.service;

import java.util.ArrayList;
import java.util.List;
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
    public Car createCar(Car car) {
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
    public void addDriverToCar(Driver driver, Car car) {
        List<Driver> updatedDrivers = new ArrayList<>();
        List<Driver> currentDrivers = car.getDrivers();
        updatedDrivers.addAll(currentDrivers);
        updatedDrivers.add(driver);
        car.setDrivers(updatedDrivers);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> updatedDrivers = new ArrayList<>();
        List<Driver> currentDrivers = car.getDrivers();
        updatedDrivers.addAll(currentDrivers);
        updatedDrivers.remove(driver);
        car.setDrivers(updatedDrivers);
        carDao.update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
