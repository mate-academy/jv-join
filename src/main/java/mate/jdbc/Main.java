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
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        Manufacturer mercedes = new Manufacturer("Mercedes-Benz", "Germany");
        Manufacturer generalMotors = new Manufacturer("General Motors", "USA");

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        manufacturerService.create(tesla);
        manufacturerService.create(mercedes);
        manufacturerService.create(generalMotors);
        System.out.println("------Saving manufacturers---------\n");
        manufacturerService.getAll().forEach(System.out::println);

        System.out.println("------Changing manufacturer---------\n");
        mercedes.setName("Mercedes");
        manufacturerService.update(mercedes);
        System.out.println(mercedes);

        Driver nick = new Driver("Nick", "123456789");
        Driver mike = new Driver("Mike", "456789223");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        System.out.println("-------Saving drivers----------\n");
        driverService.create(nick);
        driverService.create(mike);
        driverService.getAll().forEach(System.out::println);

        System.out.println("-------Changing driver----------\n");
        mike.setLicenseNumber("987654321");
        driverService.update(mike);
        System.out.println(mike);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(nick);
        drivers.add(mike);

        List<Driver> driverNick = new ArrayList<>();
        driverNick.add(nick);

        Car mercedesCar = new Car("B-CLASS ELECTRIC DRIVE", mercedes);
        mercedesCar.setDrivers(drivers);
        Car teslaCar = new Car("Model Y, the newest", tesla);
        teslaCar.setDrivers(drivers);
        Car cadillac = new Car("Cadillac CT5", generalMotors);
        cadillac.setDrivers(driverNick);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("--------Saving cars---------\n");
        carService.create(mercedesCar);
        carService.create(teslaCar);
        carService.create(cadillac);
        carService.getAll().forEach(System.out::println);

        System.out.println("--------Updating car---------\n");
        teslaCar.setModel("Model Y");
        carService.update(teslaCar);
        System.out.println(carService.get(teslaCar.getId()));

        System.out.println("--------Adding driver---------\n");
        carService.addDriverToCar(mike, cadillac);
        System.out.println(carService.get(cadillac.getId()));

        System.out.println("--------Deleting driver---------\n");
        carService.removeDriverFromCar(nick, mercedesCar);
        System.out.println(carService.delete(mercedesCar.getId()));

        System.out.println("--------Getting cars by driver---------\n");
        System.out.println(carService.getAllByDriver(mike.getId()));
    }
}
