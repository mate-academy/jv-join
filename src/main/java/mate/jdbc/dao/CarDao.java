package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);

    Car update(Car car);

    boolean delete(Long id);

    Optional<Car> get(Long id);

    List<Car> getAll();

    List<Car> getAllByDriver(Long driverId);
}
