package mate.jdbc.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.ManufacturerService;
import org.jetbrains.annotations.NotNull;

@Service
public class ManufacturerServiceImpl implements ManufacturerService {
    @Inject
    private ManufacturerDao manufacturerDao;

    @Override
    public Manufacturer create(@NotNull Manufacturer manufacturer) {
        Objects.requireNonNull(manufacturer, "manufacturer must not be null");
        return manufacturerDao.create(manufacturer);
    }

    @Override
    public Manufacturer get(@NotNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return manufacturerDao.get(id)
                .orElseThrow(() -> new NoSuchElementException("Can't get manufacturer by id: "
                        + id));
    }

    @Override
    public List<Manufacturer> getAll() {
        return manufacturerDao.getAll();
    }

    @Override
    public Manufacturer update(@NotNull Manufacturer manufacturer) {
        Objects.requireNonNull(manufacturer, "manufacturer must not be null");
        return manufacturerDao.update(manufacturer);
    }

    @Override
    public boolean delete(@NotNull Long id) {
        Objects.requireNonNull(id, "id must not be null");
        return manufacturerDao.delete(id);
    }
}
