package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {
    Car crate(Car car);

    Car get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void addDriverToCar(Car car, Driver driver);

    void removeDriverFromCar(Car car, Driver driver);

    List<Car> getAllByDriver(Long driverId);
}
