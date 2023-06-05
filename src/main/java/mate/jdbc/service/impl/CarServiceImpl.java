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
    public Car create(Car car) {
        checkCar(car, "CREATE", false);
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() ->
                new NoSuchElementException("Can't get a car from DB with ID: " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(Car car) {
        checkCar(car, "UPDATE", true);
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        checkCar(car, "ADD DRIVER TO CAR", true);
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
        checkCar(car, "REMOVE DRIVER FROM CAR", true);
        car = get(car.getId());
        if (car.getDrivers() == null || car.getDrivers().isEmpty()) {
            throw new RuntimeException(
                    "Operation failed! The car hasn't any driver. Accepted car is: " + car);
        }
        car.getDrivers().remove(driver);
        update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    private void checkCar(Car car, String operation, boolean isId) {
        if (car.getModel() == null || car.getModel().isBlank()) {
            throw new RuntimeException("Operation '" + operation + "' failed!"
                    + " Field 'model' can't be null or empty. Accepted car is: " + car);
        }
        if (car.getId() == null && isId) {
            throw new RuntimeException("Operation '" + operation + "' failed!"
                    + " This operation requires car's ID, but accepted car is: " + car);
        }
        if (car.getManufacturer() == null) {
            throw new RuntimeException("Operation '" + operation + "' failed!"
                    + " Undefined manufacturer. Accepted car is: " + car);
        }
    }
}
