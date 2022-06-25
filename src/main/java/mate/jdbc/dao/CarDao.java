package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);

    Car get(Long carId);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
