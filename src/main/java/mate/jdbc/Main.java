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

        Manufacturer audi = new Manufacturer("Audi", "German");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");

        manufacturerService.create(audi);
        manufacturerService.create(ford);
        manufacturerService.create(toyota);

        System.out.println(manufacturerService.get(audi.getId()));
        manufacturerService.update(ford);
        manufacturerService.delete(toyota.getId());
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver bob = new Driver("Bob", "146272");
        Driver john = new Driver("John", "673298");
        Driver alice = new Driver("Alice", "967037");

        driverService.create(bob);
        driverService.create(john);
        driverService.create(alice);

        System.out.println(driverService.get(bob.getId()));
        driverService.update(john);
        driverService.delete(alice.getId());
        driverService.getAll().forEach(System.out::println);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car audiQ7 = new Car("Audi Q7", audi, List.of(bob, alice));
        Car audiQ3 = new Car("Audi Q3", audi, List.of(alice, john));
        Car fordBronco = new Car("Ford Bronco", ford, List.of(bob, john));
        Car toyotaCamry = new Car("Toyota Camry", toyota, List.of(alice));

        carService.create(audiQ7);
        carService.create(audiQ3);
        carService.create(fordBronco);
        carService.create(toyotaCamry);

        System.out.println(carService.get(audiQ3.getId()));
        carService.update(toyotaCamry);
        carService.delete(audiQ7.getId());
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(alice, fordBronco);
        carService.removeDriverFromCar(alice, audiQ3);
        carService.getAllByDriver(alice.getId()).forEach(System.out::println);
    }
}
