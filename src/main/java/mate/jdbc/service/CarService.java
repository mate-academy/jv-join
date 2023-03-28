package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {
    Car create(Car car);

    Car get(Long id);

    List<Car> getAll();

    List<Car> getAllByDriver(Driver driver);

    Car update(Car car);

    boolean delete(Long id);

    void addDriver(Driver driver, Car car);

    void removeDriver(Driver driver, Car car);
}
