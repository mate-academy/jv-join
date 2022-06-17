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
        manufacturerAudi = manufacturerService.create(manufacturerAudi);
        Manufacturer manufacturerPeugeot = new Manufacturer("Peugeot", "France");
        manufacturerPeugeot = manufacturerService.create(manufacturerPeugeot);
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "123");
        bob = driverService.create(bob);
        Driver alice = new Driver("Alice", "234");
        alice = driverService.create(alice);
        Driver john = new Driver("John", "345");
        john = driverService.create(john);
        Driver penelope = new Driver("Penelope", "456");
        penelope = driverService.create(penelope);
        Driver roman = new Driver("Roman", "678");
        roman = driverService.create(roman);
        driverService.getAll().forEach(System.out::println);

        Car carAudiQ7 = new Car("Q7", manufacturerAudi, List.of(bob, alice));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carAudiQ7);
        System.out.println(carAudiQ7);
    }
}
