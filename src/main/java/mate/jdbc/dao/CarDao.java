package mate.jdbc.dao;

import java.util.List;
import java.util.Optional;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;

public interface CarDao {
    Car create(Car driver);

    Optional<Car> get(Long id);

    List<Car> getAll();

    Car update(Car driver);

    boolean delete(Long id);

    void createCarDriverRelations(Car car, List<Driver> drivers);

    void removeCarDriverRelation(Car car, Driver driver);

    List<Driver> getDriversForCar(Long id);

    List<Car> getCarsForDriver(Long id);
}
