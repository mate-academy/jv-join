package mate.jdbc.service;

import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarService {

    Car create(Car car);

    Car get(Long id);

    List<Car> getAll();

    Car update(Car car);

    boolean delete(Long id);

    void addDriverToCar(Driver driver, Car car); // these two methods

    void removeDriverFromCar(Driver driver, Car car); // should only be on service layer

    List<Car> getAllByDriver(Long driverId);
}
