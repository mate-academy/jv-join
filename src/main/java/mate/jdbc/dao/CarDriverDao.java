package mate.jdbc.dao;

import java.util.List;
import mate.jdbc.model.CarDriver;

public interface CarDriverDao {
    void create(CarDriver carDriver);

    List<CarDriver> getByDriverId(Long driverId);

    void delete(CarDriver carDriver);
}
