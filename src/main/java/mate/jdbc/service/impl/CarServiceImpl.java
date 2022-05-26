package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
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
    public Car create(Car element) {
        return carDao.create(element);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(
                () -> new NoSuchElementException("Could not get driver by id = " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(Car element) {
        return carDao.update(element);
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
        carDao.insertDriverFromCar(driver, car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        carDao.deleteDriverFromCar(driver, car);
    }
}
