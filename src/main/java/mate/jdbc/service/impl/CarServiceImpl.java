package mate.jdbc.service.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
        //checkCar(car, "CREATE", false);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() ->
                new NoSuchElementException("Can't get car with id: " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(Car car) {
        return carDao.update(car);
        //check
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        //check
        car = get(car.getId());
        if (car.getDrivers() == null || car.getDrivers().isEmpty()) {
            car.setDrivers(List.of(driver));
        } else {
            car.getDrivers().add(driver);
        }
        update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        //check
        car = get(car.getId());
        if (car.getDrivers() == null || car.getDrivers().isEmpty()) {
            throw new RuntimeException(
                    "The car with id " + car.getId() + "hasn't any driver");
        }
        car.getDrivers().remove(driver);
        update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
    //check.method
}
