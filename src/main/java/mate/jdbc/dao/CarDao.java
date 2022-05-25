package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarDao extends AbstractDao<Car> {
    void insertRelationsDrivers(Driver driver, Car car);

    void deleteRelationsDrivers(Driver driver, Car car);

    List<Car> getAllByDriver(Long driverId);
}
