package mate.jdbc.service;

import mate.jdbc.model.Car;

public interface CarService {
    Car create(Car car);

    Car get(Long id);
}
