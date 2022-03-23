package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {
    Car create(Car car);

    Car get(Long id);

    boolean delete(Long id);

    Car update(Car car);

    List<Car> getAll();

    List<Car> getAllByDriver(Long id);

    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);
}
