package mate.jdbc.service.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import java.util.List;
import java.util.ArrayList;
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
        return carDao.get(id);
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
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
    //check.method

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        car.getDrivers().add(driver);
        carDao.update(car);

        /*Car currentCarState = carDao.get(car.getId()).orElseThrow(() ->
                new NoSuchElementException("Can't get car with id: " + car.getId()));
        List<Driver> drivers = currentCarState.getDrivers();
        if (drivers == null || drivers.isEmpty()) {
            currentCarState.setDrivers(List.of(driver));
        } else {
            if (!drivers.stream().anyMatch(existingDriver -> existingDriver.getId().equals(driver.getId()))) {
                drivers.add(driver);
            }
        }
        carDao.update(currentCarState);*/
    }


    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        //check
        /*Long carId = car.getId();
        car = carDao.get(carId).orElseThrow(() ->
                new NoSuchElementException("Can't get car with id: " + carId));
        if (car.getDrivers() == null || car.getDrivers().isEmpty()) {
            throw new RuntimeException(
                    "The car with id " + car.getId() + " hasn't any driver");
        }*/
        car.getDrivers().remove(driver);
        carDao.update(car);
    }
}
