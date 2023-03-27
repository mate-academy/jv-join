package mate.jdbc.service.impl;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    CarDao carDao;

    @Override
    public Car create(Car car) {
        String query = "SELECT * FROM cars";
        return new Car(null, null);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() -> new RuntimeException("Can't find car with such an id: " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public List<Car> getAllByDriver(Driver driver) {
        return null;
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
    public void addDriver(Driver driver, Car car) {

    }

    @Override
    public void removeDriver(Driver driver, Car car) {

    }
}
