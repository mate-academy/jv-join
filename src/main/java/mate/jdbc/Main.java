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

    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        // test your code here
        initializeServices();

        Manufacturer bmw = MainHelper.createManufacturer(manufacturerService, "BMW", "Germany");
        Manufacturer volkswagen = MainHelper.createManufacturer(manufacturerService,
                "Volkswagen ", "Germany");

        Driver driver1 = MainHelper.createDriver(driverService, "Petro");
        Driver driver2 = MainHelper.createDriver(driverService, "Ivan");

        final Car x5M = MainHelper.createCar(carService, "x5M", bmw, List.of(driver1, driver2));
        final Car golf = MainHelper.createCar(carService, "golf", volkswagen, List.of(driver2));

        printAllManufacturers();
        printAllDrivers();
        printAllCars();

        carService.addDriverToCar(driver1, x5M);
        carService.removeDriverFromCar(driver2, golf);

        printAllCars();
    }

    private static void initializeServices() {
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        carService = (CarService) injector.getInstance(CarService.class);
    }

    private static void printAllManufacturers() {
        manufacturerService.getAll().forEach(System.out::println);
    }

    private static void printAllDrivers() {
        driverService.getAll().forEach(System.out::println);
    }

    private static void printAllCars() {
        carService.getAll().forEach(System.out::println);
    }
}
