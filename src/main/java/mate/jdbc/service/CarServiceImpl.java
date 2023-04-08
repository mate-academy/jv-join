package mate.jdbc.service;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao cardao;

    @Override
    public Car create(Car car) {
        return cardao.create(car);
    }

    @Override
    public Car get(Long id) {
        return cardao.get(id).get();
    }

    @Override
    public List<Car> getAll() {
        return cardao.getAll();
    }

    @Override
    public Car update(Car car) {
        return cardao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return cardao.delete(id);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return cardao.getAllByDriver(driverId);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        car.getDrivers().add(driver);
        cardao.update(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        car.getDrivers().remove(driver);
        cardao.update(car);
    }
}
