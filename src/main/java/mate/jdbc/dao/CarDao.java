package mate.jdbc.dao;

import java.sql.SQLException;
import java.util.List;
import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);

    Car get(Long id);

    List<Car> getAll() throws SQLException;

    Car update(Car car);

    boolean delete(Long id);

    List<Car> getAllByDriver(Long driverId);
}
