package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;

public interface CarDao extends GenericDao<Car> {
    List<Car> getAllByDriver(Long driverId);
}
