package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;

public interface CarDao {
    Car crate(Car car);

    Car get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void addDriver(Car car);

    void deleteDriver(Car car);

    List<Car> getAllByDriver(Long driverId);
}
