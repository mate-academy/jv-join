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
        System.out.println("Manufacturers: ");
        manufacturerService.getAll().stream().forEach(System.out::println);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        System.out.println("Drivers: ");
        driverService.getAll().stream().forEach(System.out::println);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        System.out.println("\nCars before update:");
        carService.getAll().stream().forEach(System.out::println);

        List<Car> cars = carService.getAll();
        cars.get(1).setModel("Mercedes");
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        cars.get(1).setManufacturer(manufacturers.get(2));
        carService.update(cars.get(1));
        System.out.println("Cars after update:");
        carService.getAll().stream().forEach(System.out::println);

        List<Driver> drivers = driverService.getAll();
        System.out.println("\nAll Cars by driver " + drivers.get(1));
        carService.getAllByDriver(drivers.get(1).getId())
                .stream().forEach(System.out::println);

        System.out.println("\nRemove driver from car: ");
        carService.removeDriverFromCar(drivers.get(1), cars.get(0));
        carService.getAllByDriver(drivers.get(1).getId())
                .stream().forEach(System.out::println);

        System.out.println("\nAdd driver to car: ");
        carService.addDriverToCar(drivers.get(1), cars.get(0));
        carService.getAllByDriver(drivers.get(1).getId())
                .stream().forEach(System.out::println);
    }
}
