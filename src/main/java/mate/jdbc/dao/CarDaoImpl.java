package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;

public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car driver) {
        return null;
    }

    @Override
    public Optional<Car> get(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car update(Car car) {
        return null;
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
