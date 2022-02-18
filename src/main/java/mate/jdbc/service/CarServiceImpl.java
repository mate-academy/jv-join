package mate.jdbc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        Optional<Car> carOptional = carDao.get(id);
        return carOptional.orElseThrow(() ->
                new DataProcessingException("Can't find car by id: " + id));
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
        Optional<Car> carOptional = carDao.get(car.getId());
        Car carWithOldDrivers = carOptional.orElseThrow(() ->
                new DataProcessingException("Can't get car from DB by id: " + car.getId()));
        List<Driver> drivers = new ArrayList<>(carWithOldDrivers.getDrivers());
        drivers.add(driver);
        carWithOldDrivers.setDrivers(drivers);
        carDao.update(carWithOldDrivers);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
