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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        final List<Driver> drivers = new ArrayList<>();
        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);
        Driver ivan = new Driver("Ivan", "12345");
        Driver ashot = new Driver("Ashot", "13243");
        Driver petro = new Driver("Petro", "434567");
        driverService.create(ivan);
        driverService.create(ashot);
        driverService.create(petro);
        drivers.add(ivan);
        Car mustang = new Car(ford, "mustang");
        mustang.setDrivers(drivers);
        carService.create(mustang);
        System.out.println(carService.get(mustang.getId()));
        System.out.println("----------");
        carService.removeDriverFromCar(ivan, mustang);
        carService.addDriverToCar(ashot, mustang);
        carService.addDriverToCar(petro, mustang);
        carService.getAll().forEach(System.out::println);
        System.out.println("-----------");
        carService.getAllByDriver(ashot.getId()).forEach(System.out::println);
        carService.delete(mustang.getId());
    }
}
