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
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        bmw = manufacturerService.create(bmw);
        List<Driver> drivers = new ArrayList<>();
        Driver jonny = new Driver("Jonny", "12345");
        Driver carl = new Driver("Carl", "67890");
        jonny = driverService.create(jonny);
        carl = driverService.create(carl);
        drivers.add(jonny);
        drivers.add(carl);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car x3 = new Car("X3", bmw, drivers);
        x3 = carService.create(x3);
        System.out.println(carService.get(x3.getId()));
        Driver bob = new Driver("Bob", "34567");
        bob = driverService.create(bob);
        carService.removeDriverFromCar(carl, x3);
        carService.removeDriverFromCar(jonny, x3);
        carService.addDriverToCar(bob, x3);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println();
        System.out.println(carService.getAllByDriver(bob.getId()));
        System.out.println();
        System.out.println(carService.get(x3.getId()));
        System.out.println(carService.delete(x3.getId()));
    }
}
