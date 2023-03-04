package mate.jdbc.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import mate.jdbc.dao.DriverDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Driver;

@Service
public class DriverServiceImpl implements DriverService {
    @Inject
    private DriverDao driverDao;

    @Override
    public Driver create(Driver driver) {
        Optional<Driver> driverByLicenseNumber = 
                driverDao.getByLicenseNumber(driver.getLicenseNumber());
        if (driverByLicenseNumber == null) {
            return driverDao.create(driver);
        } else {
            /*
             * I decided that the license number should be unique, so if we already have it,
             * we should return the existing object
             */
            return driverByLicenseNumber.get();
        }
    }

    @Override
    public Driver get(Long id) {
        return driverDao.get(id)
                .orElseThrow(() -> 
                new NoSuchElementException("Could not get driver by id = " + id));
    }

    @Override
    public List<Driver> getAll() {
        return driverDao.getAll();
    }

    @Override
    public Driver update(Driver driver) {
        return driverDao.update(driver);
    }

    @Override
    public boolean delete(Long id) {
        return driverDao.delete(id);
    }
}
