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
        // test your code here
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        final Driver johnDriver = driverService.create(new Driver("John", "123"));
        final Driver aliceDriver = driverService.create(new Driver("Alice", "456"));
        System.out.println(System.lineSeparator() + "All information from drivers table: ");
        driverService.getAll().forEach(System.out::println);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final Manufacturer audiManufacturer
                = manufacturerService.create(new Manufacturer("Audi", "Germany"));
        final Manufacturer bmvManufacturer
                = manufacturerService.create(new Manufacturer("BMW", "Germany"));
        System.out.println("All information from manufacturers table: ");
        manufacturerService.getAll().forEach(System.out::println);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(johnDriver);
        drivers.add(aliceDriver);
        Car audiA8 = new Car("Audi A8", audiManufacturer, drivers);
        Car bmwX5 = new Car("BMW X5", bmvManufacturer, new ArrayList<>());
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println(System.lineSeparator()
                + "All information from cars table after created cars: ");
        carService.create(audiA8);
        carService.create(bmwX5);
        carService.getAll().forEach(System.out::println);

        System.out.println(System.lineSeparator() + "All information about car with id : "
                + audiA8.getId());
        System.out.println(carService.get(audiA8.getId()));

        System.out.println(System.lineSeparator() + "Add driver with id: "
                + johnDriver.getId() + " to car with id: " + bmwX5.getId());
        carService.addDriverToCar(johnDriver, bmwX5);
        System.out.println(carService.get(bmwX5.getId()));

        System.out.println(System.lineSeparator() + "All cars for driver with id : "
                + johnDriver.getId());
        System.out.println(carService.getAllByDriver(johnDriver.getId()));

        System.out.println(System.lineSeparator() + "Remove driver with id: "
                + johnDriver.getId() + " from car with id: " + audiA8.getId());
        carService.removeDriverFromCar(johnDriver, audiA8);
        carService.getAll().forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Update car with id: " + audiA8.getId());
        Manufacturer usaManufacturer
                = manufacturerService.create(new Manufacturer("Tesla corp", "USA"));
        audiA8.setModel("Audi Q8 Tesla edition");
        audiA8.setManufacturer(usaManufacturer);
        carService.update(audiA8);
        carService.getAll().forEach(System.out::println);

        System.out.println(System.lineSeparator() + "Delete car with id: " + audiA8.getId());
        carService.delete(audiA8.getId());
        carService.getAll().forEach(System.out::println);
    }
}
