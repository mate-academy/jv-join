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
        List<Driver> drivers = new ArrayList<>();
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver alex = new Driver("Alex", "Alex817");
        Driver bob = new Driver("Bob", "Bob914");
        drivers.add(alex);
        drivers.add(bob);
        driverService.create(alex);
        driverService.create(bob);
        System.out.println("Drivers");
        driverService.getAll().forEach(System.out::println);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer corvette = new Manufacturer("Corvette", "USA");
        Manufacturer nissan1 = new Manufacturer("Nissan", "Japan");
        Manufacturer nissan2 = new Manufacturer("Nissan", "Japan");
        manufacturerService.create(corvette);
        manufacturerService.create(nissan1);
        manufacturerService.create(nissan2);
        System.out.println("Manufacturer");
        manufacturerService.getAll().forEach(System.out::println);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car xtrain = new Car("XTrain", nissan1,drivers);
        Car c8 = new Car("C8", corvette, drivers);
        carService.create(xtrain);
        carService.create(c8);
        carService.addDriverToCar(alex, c8);
        carService.addDriverToCar(bob, xtrain);
        System.out.println("CarService");
        carService.getAll().forEach(System.out::println);
        Car updatedCar = carService.get(1L);
        updatedCar.setModel("Tundra");
        updatedCar.setDrivers(drivers);
        System.out.println("Update car");
        System.out.println(carService.update(updatedCar));
        System.out.println("Get driver id");
        System.out.println(carService.getAllByDriver(alex.getId()));
        System.out.println("Delete car");
        System.out.println(carService.delete(2L));
    }
}
