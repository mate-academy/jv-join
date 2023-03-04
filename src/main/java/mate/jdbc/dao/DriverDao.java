package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Driver;

public interface DriverDao {
    Driver create(Driver driver);

    Optional<Driver> get(Long id);
    
    Optional<Driver> getByLicenseNumber(String licenseNumber);

    List<Driver> getAll();

    Driver update(Driver driver);

    boolean delete(Long id);
}
