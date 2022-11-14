package mate.jdbc.dao;

import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarDriverDao {
    void addDriverToCar(Driver driver, Car car);

    void removeDriverFromCar(Driver driver, Car car);
}
