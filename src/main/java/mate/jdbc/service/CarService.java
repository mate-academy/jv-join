package mate.jdbc.service;

import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {
    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);
}
