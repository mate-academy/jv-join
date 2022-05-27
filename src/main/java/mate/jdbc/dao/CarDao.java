package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarDao extends AbstractDao<Car> {
    List<Car> getAllByDriver(Long driverId);

    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);
}
