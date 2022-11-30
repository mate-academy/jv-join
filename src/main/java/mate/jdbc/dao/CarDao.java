package mate.jdbc.dao;

import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import java.util.List;

public interface CarDao {
    List<Car> getAll();

    Car create(Car car);

    Car get(Long id);

    Car update(Car car);

    boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
