package mate.jdbc.service;

import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

import java.util.List;

public interface CarService {
    Car create(Car car);

    Car get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void setDriverToCar(Driver driver, Car car);

    void removeDriver(Driver driver, Car car);

    List<Car> getAllByDriver(long driverId);
}
