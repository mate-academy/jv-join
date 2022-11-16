package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car driver);

    Optional<Car> get(Long id);

    List<Car> getAll();

    Car update(Car driver);

    boolean delete(Long id);

    List<Car> getAllByDriver(Long id);
}
