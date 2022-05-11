package mate.jdbc.service;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao dao;

    @Override
    public Car create(Car car) {
        return dao.create(car);
    }

    @Override
    public Car get(Long id) {
        return dao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Could not get car from DAO "
                + "by id: " + id + "."));
    }

    @Override
    public List<Car> getAll() {
        return dao.getAll();
    }

    @Override
    public Car update(Car car) {
        return dao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return dao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        drivers.add(driver);
        car.setDrivers(drivers);
        dao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> drivers = car.getDrivers();
        drivers.remove(driver);
        car.setDrivers(drivers);
        dao.update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return dao.getAllByDriver(driverId);
    }
}
