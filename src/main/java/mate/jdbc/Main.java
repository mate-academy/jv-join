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
        manufacturerService.create(new Manufacturer("Audi", "Germany"));
        manufacturerService.create(new Manufacturer("Lada", "Russia"));
        Manufacturer manufacturerTest = new Manufacturer("Zaz", "Ukraine");
        manufacturerService.create(manufacturerTest);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(new Driver("Ihor", "123"));
        driverService.create(new Driver("Bohdan", "234"));
        Driver driverTest = new Driver("Volodymyr", "345");
        driverService.create(driverTest);

        List<Driver> driversTest = new ArrayList<>();
        driversTest.add(driverTest);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carTest = new Car("Lanos", manufacturerTest, driversTest);
        System.out.println(carService.create(carTest) + " created");
        System.out.println("Get " + carService.get(16L));
        System.out.println("Get all " + carService.getAll());

        carService.addDriverToCar(driverService.get(1L), carTest);
        System.out.println(carService.getAll());

        System.out.println("GetAllByDriver " + carService.getAllByDriver(3L));

        System.out.println(carService.delete(16L) + " deleted");
        System.out.println(carService.getAll());
        System.out.println("GetAllByDriver " + carService.getAllByDriver(3L));
    }
}
