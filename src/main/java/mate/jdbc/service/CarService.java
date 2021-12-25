package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;

public interface CarService {
    Car create(Car car);

    Car get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    boolean addDriverToCar(Long carId, Long driverId);

    boolean removeDriverFromCar(Long carId, Long driverId);

    List<Car> getAllByDriver(Long driverId);

}
