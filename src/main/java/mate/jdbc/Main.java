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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer ferrari = new Manufacturer("Ferrari", "Italy");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        Manufacturer toyota = new Manufacturer("Toyota", "Germany");
        manufacturerService.create(ferrari);
        manufacturerService.create(ford);
        manufacturerService.create(toyota);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "B13654424");
        Driver alice = new Driver("Alice", "A0002144");
        Driver ted = new Driver("Ted", "G544061739250");
        driverService.create(bob);
        driverService.create(alice);
        driverService.create(ted);
        List<Driver> mustangDrivers = new ArrayList<>();
        mustangDrivers.add(bob);
        List<Driver> camryDrivers = new ArrayList<>();
        camryDrivers.add(alice);
        camryDrivers.add(ted);
        List<Driver> enzoDrivers = new ArrayList<>();
        enzoDrivers.add(ted);
        enzoDrivers.add(bob);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car mustang = new Car("Mustang", ford, mustangDrivers);
        Car camry = new Car("Camry", toyota, camryDrivers);
        Car enzo = new Car("Enzo", ferrari, enzoDrivers);
        carService.create(mustang);
        carService.create(camry);
        carService.create(enzo);
        carService.get(mustang.getId());
        System.out.println(carService.get(mustang.getId()));
        carService.getAll().forEach(System.out::println);
        carService.delete(mustang.getId());
        camry.setModel("camry3.5");
        carService.update(camry);
        carService.getAllByDriver(ted.getId()).forEach(System.out::println);
        carService.addDriverToCar(alice, enzo);
        carService.removeDriverFromCar(alice,enzo);
    }
}
