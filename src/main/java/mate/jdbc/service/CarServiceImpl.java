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
    private CarDao dao;

    @Override
    public Car create(Car car) {
        return dao.create(car);
    }

    @Override
    public Car get(Long id) {
        return dao.get(id).get();
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
        List<Driver> newDrivers = new ArrayList<>(car.getDrivers());
        newDrivers.add(driver);
        car.setDrivers(newDrivers);
        dao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        List<Driver> newDrivers = new ArrayList<>(car.getDrivers());
        newDrivers.remove(driver);
        car.setDrivers(newDrivers);
        dao.update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return dao.getAllByDriver(driverId);
    }
}
