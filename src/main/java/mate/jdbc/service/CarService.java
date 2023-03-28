package mate.jdbc.service;

import java.util.List;
import mate.jdbc.lib.Service;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

@Service
public interface CarService extends GenericService<Car> {
    void addDriverToCar(Car car, Driver driver);

    void removeDriverFromCar(Car car, Driver driver);

    List<Car> getAllByDriver(Long driverId);
}
