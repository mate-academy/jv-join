package mate.jdbc.service;

import java.util.Iterator;
import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao dao;

    @Override
    public Car create(Car car) {
        return dao.create(car);
    }

    @Override
    public Car get(Long id) {
        return dao.get(id).orElseThrow();
    }

    @Override
    public List<Car> getAll() {
        return dao.getAll();
    }

    @Override
    public Car update(Car car) {
        return dao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return dao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        car.getDrivers().add(driver);
        update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        car.getDrivers().remove(driver);
        update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<Car> cars = dao.getAll();
        boolean flag = false;
        Iterator<Car> iterator = cars.iterator();
        while (iterator.hasNext()) {
            Car car = iterator.next();
            for (Driver driver : car.getDrivers()) {
                if (driver.getId().equals(driverId)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                iterator.remove();
            }
            flag = false;
        }
        return cars;
    }
}
