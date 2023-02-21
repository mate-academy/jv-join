package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturer = new Manufacturer("Honda", "Japan");
        manufacturerService.create(manufacturer);

        Driver steve = new Driver("Steve", "11111");
        Driver sam = new Driver("Sam", "22222");
        Driver ann = new Driver("Ann", "33333");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(steve);
        drivers.add(sam);
        drivers.add(ann);
        for (Driver driver : drivers) {
            driverService.create(driver);
        }

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car honda = new Car("Accord", manufacturer, drivers);
        carService.create(honda);

        System.out.println(carService.get(honda.getId()));
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(sam.getId()).forEach(System.out::println);

        Driver bob = new Driver("Bob", "3554");
        driverService.create(bob);
        carService.removeDriverFromCar(ann, honda);
        carService.addDriverToCar(bob, honda);
        honda.setModel("Civic");
        System.out.println(carService.update(honda));

        carService.getAll().forEach(System.out::println);
        carService.delete(honda.getId());
        carService.getAll().forEach(System.out::println);
    }
}
