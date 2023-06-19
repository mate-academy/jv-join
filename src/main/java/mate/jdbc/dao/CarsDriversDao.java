package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.CarDriver;

public interface CarsDriversDao {
    CarDriver create(CarDriver driver);

    Optional<CarDriver> get(Long id);

    List<CarDriver> getAll();

    CarDriver update(CarDriver driver);

    boolean delete(Long id);
}
