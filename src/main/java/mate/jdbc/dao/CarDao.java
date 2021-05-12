package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;

@Dao
public interface CarDao {
    Car create(Car car);

    Optional<Car> get(Long id);

    Car update(Car car);

    List<Car> getAll();

    boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
