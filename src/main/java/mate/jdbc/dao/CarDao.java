package mate.jdbc.dao;

import mate.jdbc.model.Car;

public interface CarDao {
    Car create(Car car);
    
    Car get(Long id);
}
