package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.exception.DataProcessingException;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.ManufacturerService;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {
    @Inject
    private ManufacturerDao manufacturerDao;

    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        if (manufacturer == null) {
            throw new DataProcessingException("Can't create manufacturer from null");
        }
        return manufacturerDao.create(manufacturer);
    }

    @Override
    public Manufacturer get(Long id) {
        if (id == null) {
            throw new DataProcessingException("Can't get manufacturer from null");
        }
        return manufacturerDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Can't get manufacturer by id: "
                        + id));
    }

    @Override
    public List<Manufacturer> getAll() {
        return manufacturerDao.getAll();
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        if (manufacturer == null) {
            throw new DataProcessingException("Can't update manufacturer from null");
        }
        return manufacturerDao.update(manufacturer);
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) {
            throw new DataProcessingException("Can't delete manufacturer null");
        }
        return manufacturerDao.delete(id);
    }
}
