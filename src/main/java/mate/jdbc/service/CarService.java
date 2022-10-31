package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;

public interface CarService {
    Car create(Car car);

    Car get(Long id);

    List<Car> getAll();

    boolean delete(Long id);
}
