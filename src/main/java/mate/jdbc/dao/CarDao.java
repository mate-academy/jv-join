package mate.jdbc.dao;

import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import java.util.List;

public interface CarDao {
    Car create(Car car);

    Car get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);

    List<Car> getAllByDriver(Long driverId);
}
