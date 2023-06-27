package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;


@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;
    @Inject
    private DriverService driverService;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        Car car = carDao.get(id).orElseThrow(() -> new NoSuchElementException("Could not get car "
                + "by id = " + id));
        getDriverById(car);
        return car;
    }

    @Override
    public List<Car> getAll() {
        List<Car> cars = carDao.getAll();
        return getDriversById(cars);
    }

    private List<Car> getDriversById(List<Car> cars) {
        for (Car car : cars) {
            getDriverById(car);
        }
        return cars;
    }

    private void getDriverById(Car car) {
        for (Driver driver : car.getDrivers()) {
            Driver driverGetById = driverService.get(driver.getId());
            driver.setName(driverGetById.getName());
            driver.setLicenseNumber(driverGetById.getLicenseNumber());
        }
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
        car.getDrivers().add(driver);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        car.getDrivers().remove(driver);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = carDao.getAllByDriver(driverId);
        return getDriversById(cars);
    }
}
