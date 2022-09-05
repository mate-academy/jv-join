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
    private static final String PACKAGE_NAME = "mate.jdbc";

    public static void main(String[] args) {
        Injector injector = Injector.getInstance(PACKAGE_NAME);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver bombilla = driverService.create(new Driver(null, "Bombilla", "131313"));
        Driver lazyMan = driverService.create(new Driver(null, "Lazy Man", "6"));
        Driver veteran = driverService.create(new Driver(null, "Veteran", "000001"));
        Driver lucky = driverService.create(new Driver(null, "Lucky", "7777777"));

        Manufacturer mersProducer = manufacturerService
                .create(new Manufacturer(null, "Daimler AG", "Germany"));
        Manufacturer audiProducer = manufacturerService
                .create(new Manufacturer(null, "Volkswagen Group", "Germany"));

        Car mers = carService.create(new Car(null, "S600", mersProducer, new ArrayList<>()));
        Car audi = carService.create(new Car(null, "A8", audiProducer, new ArrayList<>()));
        carService.addDriverToCar(lucky, mers);
        carService.addDriverToCar(veteran, mers);
        carService.addDriverToCar(bombilla, audi);
        carService.addDriverToCar(lazyMan, audi);

        System.out.println("_____All cars in the beginning_____");
        carService.getAll().forEach(System.out::println);
        System.out.println();

        System.out.println("_____All cars for the veteran_____");
        carService.getAllByDriver(veteran.getId()).forEach(System.out::println);
        System.out.println();

        carService.removeDriverFromCar(lazyMan, audi);
        System.out.println("_________Bye-bye LazyMan_________");
        carService.getAll().forEach(System.out::println);
    }
}
