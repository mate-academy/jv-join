package mate.jdbc.dao.impl;

import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Dao;
import mate.jdbc.model.Car;

import java.util.List;
@Dao
public class CarDaoImpl implements CarDao {
    @Override
    public Car create(Car car) {
        return null;
    }

    @Override
    public Car get(Long id) {
        return null;
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
    public List<Car> getAllByDriver(Long driverId) {
        return null;
    }
}
