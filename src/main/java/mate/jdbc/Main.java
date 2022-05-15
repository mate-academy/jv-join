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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer chryslerCar = new Manufacturer("Chrysler", "USA");
        Manufacturer hondaCar = new Manufacturer("Honda", "Japan");
        manufacturerService.create(chryslerCar);
        manufacturerService.create(hondaCar);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "123212345");
        Driver alice = new Driver("Alice", "234543");
        Driver max = new Driver("Max", "345765");
        driverService.create(bob);
        driverService.create(alice);
        driverService.create(max);
        List<Driver> firstCarDrivers = new ArrayList<>();
        List<Driver> secondCarDrivers = new ArrayList<>();
        firstCarDrivers.add(bob);
        firstCarDrivers.add(alice);
        secondCarDrivers.add(max);
        secondCarDrivers.add(bob);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car firstCar = new Car("Crossfire", chryslerCar, firstCarDrivers);
        Car secondCar = new Car("Accord", hondaCar, secondCarDrivers);
        carService.create(firstCar);
        carService.create(secondCar);
        carService.getAllByDriver(firstCar.getId());
        firstCar.setManufacturer(manufacturerService.get(chryslerCar.getId()));
        carService.update(firstCar);
        carService.removeDriverFromCar(max, firstCar);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
        carService.delete(secondCar.getId());
        carService.getAll().forEach(System.out::println);
    }
}
