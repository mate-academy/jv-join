package mate.jdbc.service;

import java.util.List;
import mate.jdbc.dao.CarDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public class CarServiceImpl implements CarService {
    @Inject
    private CarDao carDao;

    @Override
    public Car get(Long id) {
        return carDao.get(id);
    }

    @Override
    public Car create(Car car) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Car> getAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Car update(Car car) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean delete(Long id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        // TODO Auto-generated method stub
        return null;
    }
}
