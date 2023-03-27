package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Driver;
import mate.jdbc.service.DriverService;

@Service
public class DriverServiceImpl implements DriverService {
    @Inject
    private DriverDao driverDao;

    @Override
    public Driver create(Driver driver) {
        Objects.requireNonNull(driver, "driver must not be null");
        return driverDao.create(driver);
    }

    @Override
    public Driver get(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return driverDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Can't get driver by id: " + id));
    }

    @Override
    public List<Driver> getAll() {
        return driverDao.getAll();
    }

    @Override
    public Driver update(Driver driver) {
        Objects.requireNonNull(driver, "driver must not be null");
        return driverDao.update(driver);
    }

    @Override
    public boolean delete(Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return driverDao.delete(id);
    }
}
