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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carSevice =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer audiManufacturer =
                new Manufacturer("Audi", "Germany");
        manufacturerService.create(audiManufacturer);
        System.out.println("Creating cars...");
        // Creating cars...
        Car audiTt = new Car("Audi TT", audiManufacturer, new ArrayList<>());
        Car audiRs = new Car("Audi RS", audiManufacturer, new ArrayList<>());
        carSevice.create(audiTt);
        carSevice.create(audiRs);
        carSevice.getAll().stream().forEach(System.out::println);
        // Adding drivers...
        System.out.println("Adding drivers...");
        Driver david = new Driver("David","255676");
        driverService.create(david);
        carSevice.addDriverToCar(david, audiTt);
        Driver danilo = new Driver("Danilo","255677");
        driverService.create(danilo);
        carSevice.addDriverToCar(danilo, audiRs);
        carSevice.addDriverToCar(david, audiRs);
        carSevice.getAll().stream().forEach(System.out::println);
        // Removing drivers...
        System.out.println("Removing drivers...");
        carSevice.removeDriverFromCar(david, audiTt);
        carSevice.removeDriverFromCar(david, audiRs);
        carSevice.getAll().stream().forEach(System.out::println);
        // Getting cars by driver...
        System.out.println("Getting cars by driver...");
        carSevice.getAllByDriver(danilo.getId())
                .stream().forEach(System.out::println);
        // Updating car...
        System.out.println("Updating car...");
        audiTt.setModel("AUDI TT");
        audiRs.setModel("AUDI RS");
        carSevice.update(audiTt);
        carSevice.update(audiRs);
        carSevice.getAll().stream().forEach(System.out::println);
        // Deleting car...
        System.out.println("Deleting car...");
        carSevice.delete(audiTt.getId());
        carSevice.delete(audiRs.getId());
        // Clear...
        manufacturerService.delete(audiManufacturer.getId());
        driverService.delete(david.getId());
        driverService.delete(danilo.getId());
    }
}
