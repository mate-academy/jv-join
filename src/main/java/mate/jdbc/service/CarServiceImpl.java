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
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Couldn't get car "
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
        if (!car.getDrivers().contains(driver)) {
            car.getDrivers().add(driver);
            carDao.update(car);
        } else {
            System.out.println("Driver " + driver + " already connected to the car " + car);
        }
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        if (car.getDrivers().contains(driver)) {
            car.getDrivers().remove(driver);
            carDao.update(car);
        } else {
            System.out.println("Driver " + driver + " does not connected to the car " + car);
        }
    }

    @Override
    public List<Car> getAllByDriverId(Long driverId) {
        return carDao.getAllCarsByDriverId(driverId);
    }
}
