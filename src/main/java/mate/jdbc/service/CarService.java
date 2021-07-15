package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {
    Car getCarById(Long id);

    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);

    List<Car> getAllCars();

    Car createCar(Car car);

    Car updateCar(Car car);
}
