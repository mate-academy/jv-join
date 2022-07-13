package mate.jdbc.dao;

import mate.jdbc.lib.Dao;

@Dao
public interface CarsDriversDao {

    boolean pairCarDriver(Long carId,Long driverId);

    boolean unpairCarDriver(Long carId,Long driverId);

}
