package mate.jdbc.dao;

import mate.jdbc.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarDao {
    Car create(Car car);

    Optional<Car> get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long carId);

    List<Car> getAllByDriver(Long driverId);
}
