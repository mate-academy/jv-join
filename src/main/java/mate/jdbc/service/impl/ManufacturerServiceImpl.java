package mate.jdbc.service.impl;

import java.util.List;
import mate.jdbc.dao.ManufacturerDao;
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
        List<Manufacturer> manufacturers;
        manufacturers = manufacturerDao.getAll();
        for (Manufacturer manufacturerItem : manufacturers) {
            if (manufacturer.getName().equals(manufacturerItem.getName())) {
                return manufacturerItem;
            }
        }
        return manufacturerDao.create(manufacturer);
    }

    @Override
    public Manufacturer get(Long id) {
        if (id == null) {
            return new Manufacturer();
        }
        return manufacturerDao.get(id).orElse(new Manufacturer());
    }

    @Override
    public List<Manufacturer> getAll() {
        return manufacturerDao.getAll();
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        List<Manufacturer> manufacturers;
        manufacturers = manufacturerDao.getAll();
        for (Manufacturer manufacturerItem : manufacturers) {
            if (manufacturer.getName().equals(manufacturerItem.getName())
                    && !manufacturerItem.getId().equals(manufacturer.getId())) {
                return manufacturerItem;
            }
        }
        return manufacturerDao.update(manufacturer);
    }

    @Override
    public boolean delete(Long id) {
        return manufacturerDao.delete(id);
    }
}
