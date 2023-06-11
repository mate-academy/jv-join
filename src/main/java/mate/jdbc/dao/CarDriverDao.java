package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.CarDriver;

public interface CarDriverDao {
    CarDriver create(CarDriver carDriver);

    boolean delete(CarDriver carDriver);

    List<Car> getAllByDriver(Long driverId);
}
