package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = new Injector("mate");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService)injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)injector.getInstance(DriverService.class);
        CarService carService = (CarService)injector.getInstance(CarService.class);
        Manufacturer alfaRomeoManufacturer = manufacturerService.create(new Manufacturer(null,
                "alfa romeo", "italy"));
        Driver roman =
                driverService.create(new Driver(null, "roman", "mn321"));
        // Creat
        Car tonale = carService.create(new Car(null, "tonale", alfaRomeoManufacturer));
        carService.getAll().forEach(System.out::println);
        System.out.println();
        // Add Driver to Car
        carService.addDriverToCar(roman, tonale);
        carService.getAll().forEach(System.out::println);
        System.out.println();
        Car stelvio = carService.create(new Car(null, "stelvio", alfaRomeoManufacturer));
        carService.getAll().forEach(System.out::println);
        System.out.println();
        carService.addDriverToCar(roman, stelvio);
        // Get all cars by driver
        carService.getAllByDriver(roman.getId()).forEach(System.out::println);
        System.out.println();
        // Update
        stelvio.setModel("STELVIO");
        carService.update(stelvio);
        carService.getAll().forEach(System.out::println);
        System.out.println();
        // Get
        System.out.println(carService.get(stelvio.getId()));
        System.out.println();
        // Remove driver from Car
        carService.removeDriverFromCar(roman, stelvio);
        System.out.println(stelvio);
        System.out.println();
        // Delete
        carService.delete(stelvio.getId());
        carService.getAll().forEach(System.out::println);
    }
}
