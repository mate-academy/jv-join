package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.exception.DataProcessingException;
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
        if (driver == null) {
            throw new DataProcessingException("Can't create driver from null");
        }
        return driverDao.create(driver);
    }

    @Override
    public Driver get(Long id) {
        if (id == null) {
            throw new DataProcessingException("Can't get driver from null");
        }
        return driverDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Can't get driver by id: " + id));
    }

    @Override
    public List<Driver> getAll() {
        return driverDao.getAll();
    }

    @Override
    public Driver update(Driver driver) {
        if (driver == null) {
            throw new DataProcessingException("Can't update driver from null");
        }
        return driverDao.update(driver);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            throw new DataProcessingException("Can't delete driver null");
        }
        return driverDao.delete(id);
    }
}
