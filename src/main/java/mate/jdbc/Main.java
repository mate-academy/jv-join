package mate.jdbc;

import java.util.Optional;
import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;

public class Main {
    public static void main(String[] args) {
        // test your code here
        CarDao dao = new CarDaoImpl();
        ManufacturerDao manDao = new ManufacturerDaoImpl();
        Manufacturer manufacturer = new Manufacturer(1L, "BMW", "Germany");
        manDao.create(manufacturer);
        Car car = new Car(1L, "M3", manufacturer);
        dao.create(car);
        Optional<Car> optionalCar = dao.get(1L);
        System.out.println(optionalCar);
        System.out.println(dao.getAll());
    }
}
