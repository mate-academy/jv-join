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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer ferrariManufacturer = new Manufacturer("Ferrari", "Italy");
        manufacturerService.create(ferrariManufacturer);

        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(toyotaManufacturer);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver bobDriver = new Driver("Bob", "1234");
        driverService.create(bobDriver);
        Driver aliceDriver = new Driver("Alice", "4567");
        driverService.create(aliceDriver);
        Driver johnDriver = new Driver("John", "7890");
        driverService.create(johnDriver);

        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        List<Driver> ferrariDrivers = new ArrayList<>();
        ferrariDrivers.add(bobDriver);
        ferrariDrivers.add(aliceDriver);
        Car ferrariSpider = new Car("Spider", ferrariManufacturer, ferrariDrivers);
        carService.create(ferrariSpider);

        List<Driver> camryDrivers = new ArrayList<>();
        camryDrivers.add(johnDriver);
        Car toyotaCamry = new Car("Camry", toyotaManufacturer, camryDrivers);
        carService.create(toyotaCamry);

        List<Driver> corollaDrivers = new ArrayList<>();
        Car toyotaCorolla = new Car("Corolla", toyotaManufacturer, corollaDrivers);
        carService.create(toyotaCorolla);

        System.out.println("All cars");
        System.out.println(carService.getAll());
        System.out.println("Car by id 1");
        System.out.println(carService.get(1L));
        System.out.println("All cars with driver with id 1");
        System.out.println(carService.getAllByDriver(ferrariSpider.getId()));
        System.out.println("Adding driver");
        carService.addDriverToCar(johnDriver, toyotaCorolla);
        System.out.println(carService.getAll());
        System.out.println("Removing driver");
        carService.removeDriverFromCar(aliceDriver, ferrariSpider);
        System.out.println(carService.getAll());
        System.out.println("Deleting car");
        carService.delete(1L);
        System.out.println(carService.getAll());
    }
}
