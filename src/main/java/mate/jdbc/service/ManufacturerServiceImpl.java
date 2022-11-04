package mate.jdbc.service;

import java.util.NoSuchElementException;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Manufacturer;

@Service
public class ManufacturerServiceImpl extends GenericServiceImpl<Manufacturer> implements ManufacturerService {
    @Inject
    private ManufacturerDao manufacturerDao;

    @Override
    public Manufacturer get(Long id) {
        return manufacturerDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Could not get manufacturer "
                        + "by id = " + id));
    }

    @Override
    protected ManufacturerDao getDao() {
        return manufacturerDao;
    }
}
