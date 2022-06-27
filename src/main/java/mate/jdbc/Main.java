package mate.jdbc;

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
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer volvo = manufacturerService.create(new Manufacturer("Volvo", "Sweden"));
        Manufacturer mercedes = manufacturerService.create(new Manufacturer("Mercedes", "Germany"));

        Driver alice = driverService.create(new Driver("Alice", "123456"));
        Driver bob = driverService.create(new Driver("Bob", "456789"));
        Driver john = driverService.create(new Driver("John", "234567"));

        Car volvoS80 = carService.create(new Car("S80", volvo, List.of(alice, bob)));

        System.out.println("Testing get() method");
        System.out.println(carService.get(volvoS80.getId()));
        System.out.println("==== ==== ==== =====");
        System.out.println("Testing getAll() method");
        Car mercedesAmg = carService.create(new Car("AMG", mercedes, List.of(john)));
        System.out.println(carService.getAll());
        System.out.println("==== ==== ==== =====");
        mercedesAmg.setModel("AMG-2");
        carService.update(mercedesAmg);
        System.out.println("Testing update method");
        System.out.println(carService.getAll());
        carService.delete(mercedesAmg.getId());
        System.out.println("Testing delete method");
        System.out.println(carService.getAll());
        System.out.println("==== ==== ==== =====");
        carService.addDriverToCar(john, volvoS80);
        System.out.println("Testing addDriverToCar() method");
        System.out.println(carService.getAll());
        System.out.println("==== ==== ==== =====");
        System.out.println("Testing removeDriverFromCar method");
        carService.removeDriverFromCar(alice, volvoS80);
        System.out.println(carService.getAll());
    }
}
