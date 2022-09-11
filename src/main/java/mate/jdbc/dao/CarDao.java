package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);

    Optional<Car> get(Long id);

    List<Car> getAll();

    List<Car> getAllCarByDriverId(Long driverId);

    Car update(Car car);

    boolean delete(Long id);
}
