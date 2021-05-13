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
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
//        Manufacturer bmw = new Manufacturer("BMW", "Germany");
//        manufacturerService.create(bmw);
//        Driver andrew = new Driver("Andrew", "7775");
//        Driver jake = new Driver("Jake", "2345");
//        Driver max = new Driver("Max", "0101");
//        Driver den = new Driver("Den", "2222");
//        driverService.create(andrew);
//        driverService.create(jake);
//        driverService.create(max);
//        driverService.create(den);
//        List<Driver> bmwX3Drivers = new ArrayList<>();
//        List<Driver> bmwX9Drivers = new ArrayList<>();
//        bmwX3Drivers.add(andrew);
//        bmwX3Drivers.add(jake);
//        bmwX9Drivers.add(max);
//        bmwX9Drivers.add(den);
//        Car bmwX3 = new Car("BMW X3", bmw, bmwX3Drivers);
//        Car bmwX9 = new Car("BMW X9", bmw, bmwX9Drivers);
//
//        System.out.println("CarService create method was called.");
//        carService.create(bmwX3);
//        carService.create(bmwX9);
//        System.out.println();
//
//        System.out.println("CarService get method was called.");
//        System.out.println("Andrew: " + carService.get(andrew.getId()));
//        System.out.println();
//
//        System.out.println("CarService delete method was called.");
//        System.out.println(carService.delete(bmwX9.getId()));
//        System.out.println();
//
//        System.out.println("CarService get by driver method was called.");
//        System.out.println("Max: " + carService.getAllByDriver(max.getId()));
//        System.out.println("Den" + carService.getAllByDriver(den.getId()));
//
//        System.out.println("CarService add driver to car method was called.");
//        Driver petya = new Driver("Petya", "8965");
//        driverService.create(petya);
//        carService.addDriverToCar(petya, bmwX3);
//
//        System.out.println("CarService update method was called.");
//        bmwX9.setName("BMW X5");
//        carService.update(bmwX9);
//
//        System.out.println("CarService get by driver was method called.");
//        System.out.println("Andrew: " + carService.getAllByDriver(andrew.getId()));
//        System.out.println("Jake: " + carService.getAllByDriver(jake.getId()));
//        System.out.println("Petya: " + carService.getAllByDriver(petya.getId()));
//
//        System.out.println("CarService remove driver from car method was called.");
//        carService.removeDriverFromCar(petya, bmwX3);
//        System.out.println("Petya was deleted from car. " + bmwX3);
//        System.out.println();
//
//        System.out.println("CarService get all method was called.");
//        carService.getAll().forEach(System.out::println);
    }
}
