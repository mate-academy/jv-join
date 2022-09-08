package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.dao.ManufacturerDao;
import mate.jdbc.dao.ManufacturerDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        ManufacturerDao manufacturerDao = new ManufacturerDaoImpl();
        Manufacturer lamborginy = new Manufacturer("Lamborgini", "Spain");
        manufacturerDao.create(lamborginy);

        Driver denis = new Driver(1L, "Denis", "123");
        Driver bob = new Driver(2L, "Bob", "234");
        Driver ann = new Driver(3L, "Ann", "345");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(denis);

        Car diablo = carService.create(new Car("Diablo", lamborginy, drivers));
        System.out.println(carService.get(diablo.getId()));

        drivers.add(bob);
        Car deo = carService.create(new Car("deo", lamborginy, drivers));
        System.out.println(carService.getAll());

        deo.setModel("lanos");
        carService.update(deo);
        carService.addDriverToCar(bob, diablo);
        carService.removeDriverFromCar(bob, deo);
        System.out.println(carService.getAllByDriver(bob.getId()));

        carService.delete(deo.getId());
        System.out.println(carService.getAll());

    }
}
