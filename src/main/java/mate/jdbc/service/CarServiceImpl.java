package mate.jdbc.service;

import mate.jdbc.dao.CarDao;;
import mate.jdbc.dao.DriverDao;
import mate.jdbc.lib.Inject;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarServiceImpl implements CarServic{
    private CarDao carDao;
    private DriverDao driverDao;

    public CarServiceImpl(CarDao carDao, DriverDao driverDao) {
        this.carDao = carDao;
        this.driverDao = driverDao;
    }

    @Override
    public Car create(Car car) {
        return carDao.create(car);
    }

    @Override
    public Car get(Long id) {
        return carDao.get(id).orElseThrow(() ->
                new NoSuchElementException("Could not get car "
                + "by id = " + id));
    }

    @Override
    public List<Car> getAll() {
        return carDao.getAll();
    }

    @Override
    public Car update(Car car) {
        return carDao.update(car);
    }

    @Override
    public boolean delete(Long id) {
        return carDao.delete(id);
    }

    @Override
    public void addDriverToCar(Driver driver, Car car) {
        Driver driverAddCar = driverDao.create(driver);
        List<Driver> driverList = List.of(driverAddCar);
        car.setDriverList(driverList);
        create(car);
    }

    @Override
    public void removeDriverFromCar(Driver driver, Car car) {
        for (Driver driver1 : car.getDriverList()){
          if (driver.getId().equals(driver1.getId()) &&
                  driver.getName().equals(driver1.getName()) ){
              driverDao.delete(driver.getId());
          }
        }
    }

    @Override
    public List<Car> getAllByDriver(Long driverId) {
        return carDao.getAllByDriver(driverId);
    }
}
