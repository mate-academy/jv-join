package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.util.Injector;

public class Main {
    private static final Injector injector
            = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        System.out.println("ADDING MANUFACTURERS TO DB:");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = List.of(
                new Manufacturer("Tesla", "USA"),
                new Manufacturer("Lucid", "USA"));
        if (manufacturerService.getAll().isEmpty()) {
            for (Manufacturer manufacturer : manufacturers) {
                manufacturerService.create(manufacturer);
            }
        }
        System.out.println("Done");

        System.out.println("ADDING DRIVERS TO DB:");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = List.of(
                new Driver("John", "1234"),
                new Driver("Bill", "2345"),
                new Driver("Mike", "3456"),
                new Driver("Bob", "4567"),
                new Driver("Alice", "5678"));
        if (driverService.getAll().isEmpty()) {
            for (Driver driver : drivers) {
                driverService.create(driver);
            }
        }
        List<Driver> teslaDrivers = new ArrayList<>();
        teslaDrivers.add(driverService.get(1L));
        teslaDrivers.add(driverService.get(2L));

        List<Driver> lucidDrivers = new ArrayList<>();
        lucidDrivers.add(driverService.get(3L));
        lucidDrivers.add(driverService.get(4L));
        System.out.println("Done");

        System.out.println("START TESTING:");
        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        System.out.println("CarService create method was called.");
        Car teslaModelY = new Car("Model Y", manufacturerService.get(1L), teslaDrivers);
        carService.create(teslaModelY);
        Car lucidAir = new Car("Air", manufacturerService.get(2L), lucidDrivers);
        carService.create(lucidAir);
        System.out.println();

        System.out.println("CarService get method was called.");
        System.out.println(carService.get(driverService.get(1L).getId()));
        System.out.println();

        System.out.println("CarService delete method was called.");
        System.out.println(carService.delete(lucidAir.getId()));
        System.out.println();

        System.out.println("CarService get by driver method was called.");
        System.out.println(carService.getAllByDriver(driverService.get(3L).getId()));
        System.out.println(carService.getAllByDriver(driverService.get(4L).getId()));
        System.out.println();

        System.out.println("CarService add driver to car method was called.");
        carService.addDriverToCar(driverService.get(5L), teslaModelY);
        System.out.println();

        System.out.println("CarService update method was called.");
        teslaModelY.setModel("Model 3");
        carService.update(teslaModelY);
        System.out.println();

        System.out.println("CarService get by driver was method called.");
        System.out.println(driverService.get(1L).getName()
                + ":" + carService.getAllByDriver(driverService.get(1L).getId()));
        System.out.println(driverService.get(2L).getName()
                + ":" + carService.getAllByDriver(driverService.get(2L).getId()));
        System.out.println(driverService.get(5L).getName()
                + ":" + carService.getAllByDriver(driverService.get(5L).getId()));
        System.out.println();

        System.out.println("CarService remove driver from car method was called.");
        carService.removeDriverFromCar(driverService.get(5L), teslaModelY);
        System.out.println(driverService.get(5L).getName()
                + " removed from car " + carService.get(1L));
        System.out.println();

        System.out.println("CarService get all method was called.");
        carService.getAll().forEach(System.out::println);
        System.out.println("ALL TESTS FINISHED");
    }
}
