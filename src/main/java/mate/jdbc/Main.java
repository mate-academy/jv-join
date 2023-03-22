package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.impl.CarServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static CarService carDao = (CarServiceImpl) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(1L, "Ivan", "123456"));
        Manufacturer manufacturer = new Manufacturer(1L, "BMW", "Germany");
        Car car = new Car("X6", manufacturer, drivers);
        car.setId(2L);
        carDao.create(car);
        carDao.addDriverToCar(new Driver(2L, "Mykola", "671241"), car);
        carDao.removeDriverFromCar(new Driver(2L, "Mykola", "671241"), car);
        carDao.delete(1L);
        System.out.println(carDao.get(2L));
        Car carUpdated = new Car("X7", manufacturer, drivers);
        carDao.update(carUpdated);
        System.out.println(carDao.get(2L));
        System.out.println(carDao.getAll());
    }
}
