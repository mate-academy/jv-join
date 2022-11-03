package mate.jdbc.service;

import java.util.NoSuchElementException;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.dao.GenericDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Driver;

@Service
public class DriverServiceImpl extends GenericServiceImpl<Driver> implements DriverService {
    @Inject
    private DriverDao driverDao;

    @Override
    public Driver get(Long id) {
        return driverDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Could not get driver "
                        + "by id = " + id));
    }

    @Override
    protected GenericDao<Driver> getDao() {
        return driverDao;
    }
}
