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
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audi);
        manufacturerService.create(bmw);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver ann = new Driver("Ann", "975125");
        Driver bob = new Driver("Bob", "779645");
        driverService.create(ann);
        driverService.create(bob);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(ann);
        drivers.add(bob);
        Car x5 = new Car();
        x5.setModel("X5");
        x5.setManufacturer(bmw);
        x5.setDrivers(drivers);
        Car q7 = new Car();
        q7.setModel("Q7");
        q7.setManufacturer(audi);
        q7.setDrivers(drivers);
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(x5);
        carService.create(q7);
        System.out.println(carService.get(1L));
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(ann, x5);
        carService.addDriverToCar(bob, q7);
        carService.getAllByDriver(ann.getId()).forEach(System.out::println);
        carService.addDriverToCar(ann, x5);
        carService.addDriverToCar(bob, x5);
        carService.getAllByDriver(ann.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(bob, x5);
        carService.delete(x5.getId());
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
    }
}
