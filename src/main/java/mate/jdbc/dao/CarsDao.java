package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;

public interface CarsDao {
    Car create(Car car);

    Optional<Car> get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    Optional<Car> findByModelAndManufacturer(String model, Manufacturer manufacturer);

    List<Car> getAllByDriver(Long driverId);
}
