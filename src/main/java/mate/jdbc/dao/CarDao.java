package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public interface CarDao {
    public Car create(Car car);

    public Optional<Car> get(Long id);

    public List<Car> getAll();

    public Car update(Car car);

    public boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
