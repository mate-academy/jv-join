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
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        toyota = manufacturerService.create(toyota);
        bmw = manufacturerService.create(bmw);
        System.out.println("Create 2 manufacturers");
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver mike = new Driver("Mike", "AE4552UA");
        Driver bob = new Driver("Bob", "BOB");
        driverService.create(mike);
        driverService.create(bob);
        System.out.println("Add 2 drivers");
        driverService.getAll().forEach(System.out::println);

        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(mike);
        List<Driver> bmwDrivers = new ArrayList<>();
        toyotaDrivers.add(bob);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car toyotaCar = new Car("Corola", toyota, toyotaDrivers);
        Car bmwCar = new Car("i4", bmw, bmwDrivers);
        System.out.println(carService.getAll());
        System.out.println("Create 2 cars");

        carService.addDriverToCar(bob,toyotaCar);
        System.out.println(carService.get(toyotaCar.getId()));
        System.out.println("Add driver in Toyota");
    }
}
