package mate.jdbc.service.impl;

import java.util.List;
import java.util.Objects;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.lib.exception.DataProcessingException;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

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
                .orElseThrow(() -> new DataProcessingException("Could not get car by id " + id));
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
        for (Driver driverForCar : car.getDrivers()) {
            if (Objects.equals(driver, driverForCar)) {
                throw new DataProcessingException("Can't add new " + driver + " to " + car + "! "
                        + "Driver is already driving this car!");
            }
        }
        List<Driver> drivers = car.getDrivers();
        drivers.add(driver);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        car.getDrivers().removeIf(driverForCar -> Objects.equals(driver, driverForCar));
        carDao.update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
