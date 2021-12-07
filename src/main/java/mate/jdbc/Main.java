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
        Manufacturer chevrolet = new Manufacturer("Chevrolet", "USA");
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        manufacturerService.create(chevrolet);
        manufacturerService.create(mercedes);

        Driver bob = new Driver("Bob", "1");
        Driver john = new Driver("John", "2");
        Driver steve = new Driver("Steve", "3");

        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        driverService.create(bob);
        driverService.create(john);
        driverService.create(steve);

        List<Driver> chevroletDrivers = new ArrayList<>();
        chevroletDrivers.add(bob);
        chevroletDrivers.add(john);

        List<Driver> mercedesDrivers = new ArrayList<>();
        mercedesDrivers.add(bob);
        mercedesDrivers.add(john);

        Car chevroletCar = new Car("Aveo", chevrolet, chevroletDrivers);
        Car mercedesCar = new Car("Maybach", mercedes, mercedesDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        carService.create(chevroletCar);
        carService.create(mercedesCar);

        carService.getAll().forEach(System.out::println);

        mercedesCar.setModel("S-class");

        carService.delete(chevroletCar.getId());
        carService.removeDriverFromCar(bob, mercedesCar);
        carService.addDriverToCar(steve, mercedesCar);
        carService.update(mercedesCar);

        carService.getAllByDriver(bob.getId());

        carService.getAll().forEach(System.out::println);
    }
}
