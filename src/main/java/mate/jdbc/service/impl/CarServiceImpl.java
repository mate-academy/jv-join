package mate.jdbc.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car create(Car car) {
        final Car created = carDao.create(car);
        mergeCarDrivers(car.getId(), car.getDrivers());
        return created;
    }

    @Override
    public Car get(Long id) {
        final Car car = carDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Could not get car "
                        + "by id = " + id));
        car.setDrivers(carDao.queryDrivers(car.getId()));
        return car;
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll().stream()
                .peek(c ->
                        c.setDrivers(carDao.queryDrivers(c.getId()))
                ).collect(Collectors.toList());
    }

    @Override
    public Car update(Car car) {
        final Car updated = carDao.update(car);
        mergeCarDrivers(car.getId(), car.getDrivers());
        return updated;
    }

    @Override
    public boolean delete(Long id) {
        //deleting links with drivers for car before deleting car
        mergeCarDrivers(id, new HashSet<>());
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Car car, Driver driver) {
        Set<Driver> newDrivers = new HashSet<>(car.getDrivers());
        newDrivers.add(driver);
        car.setDrivers(newDrivers);
        update(car);
    }

    @Override
    public void removeDriverFromCar(Car car, Driver driver) {
        final Set<Driver> newDrivers = new HashSet<>(car.getDrivers());
        newDrivers.remove(driver);
        car.setDrivers(newDrivers);
        update(car);
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }

    private void mergeCarDrivers(Long carId, Set<Driver> newDrivers) {
        final Set<Driver> existing = carDao.queryDrivers(carId);
        final Set<Driver> toRemove = existing.stream()
                .filter(o -> !newDrivers.contains(o))
                .collect(Collectors.toSet());
        final Set<Driver> toAdd = newDrivers.stream()
                .filter(o -> !existing.contains(o))
                .collect(Collectors.toSet());
        toRemove.forEach(d -> carDao.removeLink(carId, d.getId()));
        toAdd.forEach(a -> carDao.addLink(carId, a.getId()));
    }
}
