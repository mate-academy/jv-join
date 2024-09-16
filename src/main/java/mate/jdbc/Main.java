package mate.jdbc;

import java.util.ArrayList;
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
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("audi", "Germany");
        Car audiA7 = new Car("a7", new ArrayList<>(), manufacturer);
        Car audiA5 = new Car("a5", new ArrayList<>(), manufacturer);
        manufacturerService.create(manufacturer);
        carService.create(audiA5);
        carService.create(audiA7);
        System.out.println(carService.getAll());
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver kostya = new Driver("Kostya", "444");
        Driver anya = new Driver("Anya", "555");
        Driver kolya = new Driver("Kolya", "666");
        driverService.create(kostya);
        driverService.create(anya);
        driverService.create(kolya);
        carService.addDriverToCar(kostya, audiA7);
        carService.addDriverToCar(anya, audiA5);
        carService.addDriverToCar(kolya, audiA5);
        carService.getAllByDriver(kostya.getId()).forEach(System.out::println);
        carService.addDriverToCar(kostya, audiA5);
        carService.getAllByDriver(kostya.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(kolya, audiA5);
        carService.getAllByDriver(kostya.getId()).forEach(System.out::println);
    }
}
