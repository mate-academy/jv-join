package mate.jdbc.dao;

import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import java.util.List;
import java.util.Optional;

@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
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

    @Override
    public void addDriverToCar(Driver driver, Car car) {

    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {

    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}
