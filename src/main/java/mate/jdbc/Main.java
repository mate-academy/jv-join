package mate.jdbc;

import java.util.List;
import java.util.stream.Collectors;
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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        info("Creating drivers result: ");
        List<Driver> driversToCreate = List.of(
                new Driver(null, "Bob", "1d1d1d1d1d1d1d1d"),
                new Driver(null, "John", "2d2d2d2d2d22d2d"));
        List<Driver> createdDrivers = driversToCreate.stream()
                .map(driverService::create)
                .collect(Collectors.toList());
        createdDrivers.forEach(System.out::println);
        final Driver bob = createdDrivers.get(0);
        final Driver john = createdDrivers.get(1);

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        info("Creating manufacturers result: ");
        List<Manufacturer> manufacturersToCreate = List.of(
                new Manufacturer(null, "Audi", "Ukraine"),
                new Manufacturer(null, "Land Rover", "Ukraine"));
        List<Manufacturer> createdManufacturers = manufacturersToCreate.stream()
                .map(manufacturerService::create)
                .collect(Collectors.toList());
        createdManufacturers.forEach(System.out::println);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        info("Creating Cars result: ");
        final Car audi = carService.create(
                new Car(null, "Q7", createdManufacturers.get(0), createdDrivers));
        Car landRover = carService.create(
                new Car(null, "Discovery", createdManufacturers.get(1), createdDrivers));
        carService.getAll().forEach(System.out::println);

        info("Update car model result: ");
        landRover.setModel("Sport");
        carService.update(landRover);
        carService.getAll().forEach(System.out::println);

        info("After removing drivers: ");
        carService.removeDriverFromCar(bob, landRover);
        carService.getAll().forEach(System.out::println);

        info("After adding drivers: ");
        landRover.setModel("Sport");
        carService.addDriverToCar(bob, landRover);
        carService.getAll().forEach(System.out::println);

        info("Get car by id result: ");
        System.out.println(carService.get(audi.getId()));

        info("Get all cars by driver result: ");
        Car newAudi = carService.create(new Car(null, "Q7",
                createdManufacturers.get(0), List.of(john)));
        carService.getAllByDriver(john.getId()).forEach(System.out::println);

        info("After removing car: ");
        carService.delete(newAudi.getId());
        carService.getAll().forEach(System.out::println);
    }

    public static void info(String message) {
        System.out.println(System.lineSeparator() + message);
    }
}
