package mate.jdbc.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        return carDao.getAllByDriver();
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
            update(car);
            return;
        }
        Set<Driver> drivers = new HashSet<>();
        drivers.add(driver);
        car.setDrivers(drivers);
        update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (car.getDrivers() != null && !car.getDrivers().isEmpty()) {
            car.getDrivers().remove(driver);
            update(car);
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
        return carDao.getAllByDriver(driverId);
    }
}
