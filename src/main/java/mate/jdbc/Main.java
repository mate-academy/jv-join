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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("France");
        manufacturer.setName("Peugeot");
        manufacturerService.create(manufacturer);

        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("03321");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(bob);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("033231121");
        driverService.create(john);
        drivers.add(john);

        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("308");
        car.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        Driver alice = new Driver("Alice", "22222222");
        driverService.create(alice);

        carService.addDriverToCar(alice, car);
        car.setModel("307");
        carService.update(car);
        carService.getAll().forEach(System.out::println);
        carService.get(car.getId());
        carService.delete(car.getId());
        carService.getAllByDriver(john.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(alice, car);
        System.out.println(carService.get(car.getId()));

    }
}
