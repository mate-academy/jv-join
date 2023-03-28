package mate.jdbc.dao;

import java.util.List;
import java.util.Set;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarDao extends GenericDao<Car> {
    List<Car> getAllByDriver(Long driverId);

    Set<Driver> queryDrivers(Long carId);

    void addLink(Long carId, Long driverId);

    void removeLink(Long carId, Long driverId);
}
