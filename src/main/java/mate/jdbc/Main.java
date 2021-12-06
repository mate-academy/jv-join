package mate.jdbc;

import java.util.ArrayList;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Car x5 = new Car("X5", bmw, new ArrayList<>());
        manufacturerService.create(bmw);
        Car q7 = new Car("Q7", audi, new ArrayList<>());
        manufacturerService.create(audi);
        carService.create(x5);
        carService.create(q7);
        carService.getAll().forEach(System.out::println);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver ann = new Driver("Ann", "964544");
        Driver bob = new Driver("Bob", "755345");
        driverService.create(ann);
        driverService.create(bob);
        carService.addDriverToCar(ann, x5);
        carService.addDriverToCar(bob, q7);
        carService.getAllByDriver(ann.getId()).forEach(System.out::println);
        carService.addDriverToCar(ann, x5);
        carService.getAllByDriver(ann.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(bob, x5);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
    }
}
