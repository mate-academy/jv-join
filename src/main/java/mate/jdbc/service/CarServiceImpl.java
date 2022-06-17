package mate.jdbc.service;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.CarsDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarsDao carsDao;

    @Override
    public Car create(Car car) {
        return carsDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carsDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Could not get car by id = " + id));
    }

    @Override
    public List<Car> getAll() {
        return carsDao.getAll();
    }

    @Override
    public Car update(Car car) {
        return carsDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return carsDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}
