package mate.jdbc.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import mate.jdbc.dao.CarDao;
import mate.jdbc.exception.DataProcessingException;
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
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id)
                .orElseThrow(() -> new RuntimeException("There is no car with id " + id));
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
            for (Driver oldDriver : car.getDrivers()) {
                if (oldDriver.getId().equals(driver.getId())) {
                    throw new RuntimeException("The driver " + driver
                            + "is already attached to the car " + car + ".");
                }
            }
            car.getDrivers().add(driver);
        } else {
            List<Driver> drivers = new ArrayList<>();
            drivers.add(driver);
            car.setDrivers(drivers);
        }
        Car carWithNewDriver = car.getId() == null ? carDao.create(car) : carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (car.getDrivers().remove(driver)) {
            carDao.update(car);
        } else {
            throw new DataProcessingException("The Driver " + driver
                    + "isn't attached to the car " + car + ".");
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAll().stream()
                .filter(dl -> dl.getDrivers()
                        .stream()
                        .anyMatch(d -> d.getId().equals(driverId)))
                .collect(Collectors.toList());
    }
}
