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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer gmc = new Manufacturer("GMC", "USA");
        Manufacturer honda = new Manufacturer("Honda", "Japan");
        manufacturerService.create(audi);
        manufacturerService.create(gmc);
        manufacturerService.create(honda);
        System.out.println("===All records after Manufacturer`s create-block===");
        manufacturerService.getAll().forEach(System.out::println);
        manufacturerService.delete(gmc.getId());
        manufacturerService.update(new Manufacturer(honda.getId(), "Hyundai", "South Korea"));
        System.out.println("===All records after modified-block===");
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println("===Get`s the first record===");
        System.out.println(manufacturerService.get(audi.getId()));

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver alice = new Driver("Alice", "123456789");
        Driver bob = new Driver("Bob", "987654321");
        Driver john = new Driver("John", "43219875");
        driverService.create(alice);
        driverService.create(bob);
        driverService.create(john);
        System.out.println("===All records after Driver`s create-block===");
        driverService.getAll().forEach(System.out::println);
        driverService.delete(john.getId());
        driverService.update(new Driver(alice.getId(), "Alice", "111111111"));
        System.out.println("===All records after modified-block===");
        driverService.getAll().forEach(System.out::println);
        System.out.println("===Get`s the first record===");
        System.out.println(driverService.get(alice.getId()));
        System.out.println();

        Car gmcCanyon = new Car("Canyon", "123-4567", gmc);
        Car audiA8 = new Car("A8", "978-645", audi);
        Car hondaAccord = new Car("Accord", "456-789", honda);
        List<Driver> gmcCanyonDrivers = List.of(alice);
        List<Driver> audiA8Drivers = List.of(bob);
        List<Driver> hondaAccordDrivers = List.of(alice, bob);
        gmcCanyon.setDrivers(gmcCanyonDrivers);
        audiA8.setDrivers(audiA8Drivers);
        hondaAccord.setDrivers(hondaAccordDrivers);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        System.out.println("===All records after Car`s create-block===");
        final Car storedAudiA8 = carService.create(audiA8);
        final Car storedHondaAccord = carService.create(hondaAccord);
        final Car storedGmcCanyon = carService.create(gmcCanyon);
        carService.getAll().forEach(System.out::println);

        System.out.println("-- Before car modified --");
        System.out.println(storedGmcCanyon);
        storedGmcCanyon.setRegistrationNumber("0000-0000");
        System.out.println("-- After car modified");
        System.out.println(carService.update(storedGmcCanyon));

        System.out.println("-- Before car delete --");
        System.out.println(carService.getAll());
        System.out.println("-- After car delete --");
        carService.delete(storedAudiA8.getId());
        System.out.println(carService.getAll());

        System.out.println("-- Add Driver to Car --");
        Driver ivan = new Driver("Ivan", "7777-7777");
        Driver storedIvan = driverService.create(ivan);
        carService.addDriverToCar(storedIvan, storedHondaAccord);
        System.out.println(storedHondaAccord);
        System.out.println("-- Remove Driver from Car --");
        carService.removeDriverFromCar(storedIvan, storedHondaAccord);
        System.out.println(storedHondaAccord);

        System.out.println("-- Get All Cars by Driver --");
        carService.getAllByDriver(alice.getId()).forEach(System.out::println);
    }
}
