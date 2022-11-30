package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;

public interface CarDao {
    List<Car> getAll();

    Car create(Car car);

    Car get(Long id);

    Car update(Car car);

    boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
