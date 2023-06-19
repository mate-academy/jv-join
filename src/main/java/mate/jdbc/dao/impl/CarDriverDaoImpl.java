package mate.jdbc.dao.impl;

import java.util.List;
import java.util.Optional;
import mate.jdbc.dao.CarsDriversDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.CarDriver;

@Dao
public class CarDriverDaoImpl implements CarsDriversDao {
    @Override
    public CarDriver create(CarDriver driver) {
        return null;
    }

    @Override
    public Optional<CarDriver> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<CarDriver> getAll() {
        return null;
    }

    @Override
    public CarDriver update(CarDriver driver) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
