package mate.jdbc;

import java.util.List;
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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerAudi = new Manufacturer("Audi", "Germany");
        manufacturerAudi = manufacturerService.createOrGet(manufacturerAudi);
        Manufacturer manufacturerPeugeot = new Manufacturer("Peugeot", "France");
        manufacturerPeugeot = manufacturerService.createOrGet(manufacturerPeugeot);
        Manufacturer manufactureBmw = new Manufacturer("BMW", "Germany");
        manufactureBmw = manufacturerService.createOrGet(manufactureBmw);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "123");
        bob = driverService.createOrGet(bob);
        Driver alice = new Driver("Alice", "234");
        alice = driverService.createOrGet(alice);
        Driver john = new Driver("John", "345");
        john = driverService.createOrGet(john);
        Driver penelope = new Driver("Penelope", "456");
        penelope = driverService.createOrGet(penelope);
        Driver roman = new Driver("Roman", "678");
        roman = driverService.createOrGet(roman);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carAudiQ7 = new Car("Q7", manufacturerAudi, List.of(bob, alice));
        carAudiQ7 = carService.createOrGet(carAudiQ7);
        Car carPeugeot508 = new Car("508", manufacturerPeugeot, List.of(john, penelope));
        carPeugeot508 = carService.createOrGet(carPeugeot508);
        Car carBmwI3 = new Car("i3", manufactureBmw, List.of(roman));
        carBmwI3 = carService.createOrGet(carBmwI3);

        carService.addDriverToCar(bob, carAudiQ7);
        carService.addDriverToCar(bob, carPeugeot508);
        carService.addDriverToCar(bob, carBmwI3);
        System.out.println("getAllByDriver('Bob')");
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);

        carService.removeDriverFromCar(bob, carAudiQ7);
        System.out.println("getAll()");
        carService.getAll().forEach(System.out::println);
    }
}
