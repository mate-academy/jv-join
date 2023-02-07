package mate.jdbc.service;

import java.util.Iterator;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
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
        return carDao.get(id);
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
        CarDaoImpl carDaoImpl = new CarDaoImpl();
        List<Driver> driversFromCar = carDaoImpl.getDriversFromCar(car.getId());
        driversFromCar.add(driver);
        car.setDrivers(driversFromCar);
        carDao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        CarDaoImpl carDaoImpl = new CarDaoImpl();
        List<Driver> driversFromCar = carDaoImpl.getDriversFromCar(car.getId());

        Iterator<Driver> driverIterator = driversFromCar.iterator();
        while (driverIterator.hasNext()) {
            Driver nextDriver = driverIterator.next();
            if (nextDriver.equals(driver)) {
                driverIterator.remove();
            }
        }
        car.setDrivers(driversFromCar);
        carDao.update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
