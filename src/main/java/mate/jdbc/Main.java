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
        Manufacturer manufacturer1 = new Manufacturer("Audi", "Germany");
        Manufacturer manufacturer2 = new Manufacturer("Lada", "Russia");
        Manufacturer manufacturer3 = new Manufacturer("Zaz", "Ukraine");

        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.create(manufacturer3);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Ihor", "123");
        Driver driver2 = new Driver("Bohdan", "234");
        Driver driver3 = new Driver("Volodymyr", "345");

        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);

        List<Driver> driversTest = new ArrayList<>();
        driversTest.add(driver3);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carTest = new Car("Lanos", manufacturer3, driversTest);
        System.out.println(carService.create(carTest) + " created");
        System.out.println("Get " + carService.get(carTest.getId()));
        System.out.println("Get all " + carService.getAll());

        carService.addDriverToCar(driverService.get(driver1.getId()), carTest);
        System.out.println(carService.getAll());

        System.out.println("GetAllByDriver " + carService.getAllByDriver(driver3.getId()));

        System.out.println(carService.delete(carTest.getId()) + " deleted");
        System.out.println(carService.getAll());
        System.out.println("GetAllByDriver " + carService.getAllByDriver(driver3.getId()));
    }
}
