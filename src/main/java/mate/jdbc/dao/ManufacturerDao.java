package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Manufacturer;

public interface ManufacturerDao {
    Manufacturer create(Manufacturer driver);

    Optional<Manufacturer> get(Long id);

    List<Manufacturer> getAll();

    Manufacturer update(Manufacturer driver);

    boolean delete(Long id);
}
