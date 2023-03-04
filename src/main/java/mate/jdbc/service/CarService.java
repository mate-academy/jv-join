package mate.jdbc.service;

import mate.jdbc.model.Driver;
import java.util.List;
import mate.jdbc.model.Car;

public interface CarService {
    Car create(Car car);
    
    Car get(Long id);
    
    List<Car> getAll();
    
    Car update(Car car);
    
    boolean delete(Long id);
    
    //Do not implement following methods in DAO layer, only in service layer. Use update method from DAO
    void addDriverToCar(Driver driver, Car car);
    
    void removeDriverFromCar(Driver driver, Car car);
    
    List<Car> getAllByDriver(Long driverId);
}
