package mate.jdbc.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDriverDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.CarDriver;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;
    @Inject
    private CarDriverDao carDriverDao;

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() -> new NoSuchElementException("Could not get car "
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
        CarDriver carDriver = new CarDriver(car.getId(), driver.getId());
        carDriverDao.create(carDriver);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        CarDriver carDriver = new CarDriver(car.getId(), driver.getId());
        carDriverDao.delete(carDriver);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        List<CarDriver> carsDriverList = carDriverDao.getByDriverId(driverId);
        return carsDriverList.stream()
                .map(e -> carDao.get(e.getCarId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
