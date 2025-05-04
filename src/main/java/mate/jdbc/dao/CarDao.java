package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);

    Optional<Car> get(Long id);

    boolean delete(Long id);

    List<Car> getAll();

    Car update(Car car);

    List<Car> getAllByDriver(Long driverId);
}
