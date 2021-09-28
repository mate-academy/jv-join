package mate.jdbc.service;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id)
                .orElseThrow(() -> new DataProcessingException("Could not get car from DAO "
                + "by id = " + id));
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
        if (car.getDrivers() != null) {
            car.getDrivers().add(driver);
            return;
        }
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        car.setDrivers(drivers);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (car.getDrivers() != null) {
            car.getDrivers().remove(driver);
            return;
        }
        throw new RuntimeException("Can't remove driver from '"
                + car.getManufacturer().getName() + " " + car.getModel() + "'!"
                + System.lineSeparator()
                + " You have not yet added any drivers to '"
                + car.getManufacturer().getName() + " " + car.getModel() + "'!");
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = getAll();
        List<Car> carsByDriver = new ArrayList<>();
        for (Car car : cars) {
            long count = car.getDrivers().stream()
                    .map(Driver::getId)
                    .filter(id -> id.equals(driverId))
                    .count();
            if (count > 0) {
                carsByDriver.add(car);
            }
        }
        return carsByDriver;
    }
}
