package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarDao extends AbstractDao<Car> {
    List<Car> getAllByDriver(Long driverId);

    void insertDriverFromCar(Driver driver, Car car);

    void deleteDriverFromCar(Driver driver, Car car);
}
