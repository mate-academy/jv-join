package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);

    Optional<Car> get(Long id);

    List<Car> getAll();

    public Car update(Car car);

    public void deleteRelationsForCar(Long carId);

    public void insertDrivers(Car car);

    boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
