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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerTesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(manufacturerTesla);
        Manufacturer manufacturerKia = new Manufacturer("Kia", "Korea");
        manufacturerService.create(manufacturerKia);
        Manufacturer manufacturerDS = new Manufacturer("Citroen", "France");
        manufacturerService.create(manufacturerDS);
        System.out.println("Creating manufactures: ");
        manufacturerService.getAll().forEach(System.out::println);

        System.out.println("Changing manufacturers name: ");
        manufacturerDS.setName("DS");
        manufacturerService.update(manufacturerDS);
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverOscar = new Driver("Oscar", "1ldlvik3");
        driverService.create(driverOscar);
        Driver driverSanta = new Driver("Santa", "ldk4mc6p");
        driverService.create(driverSanta);
        Driver driverKevin = new Driver("Kevin", "tandkf86");
        driverService.create(driverKevin);
        System.out.println("Creating drivers: ");
        driverService.getAll().forEach(System.out::println);

        System.out.println("Changing drivers: ");
        driverOscar.setName("Oscarino");
        driverService.update(driverOscar);
        driverService.getAll().forEach(System.out::println);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverOscar);
        drivers.add(driverKevin);
        drivers.add(driverSanta);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car teslaCar = new Car("S", manufacturerTesla);
        teslaCar.setDrivers(drivers);
        carService.create(teslaCar);
        Car kiaCar = new Car("Soul", manufacturerKia);
        kiaCar.setDrivers(drivers);
        carService.create(kiaCar);
        Car dsCar = new Car("3 Crossback", manufacturerDS);
        dsCar.setDrivers(drivers);
        carService.create(dsCar);
        System.out.println("Creating cars: ");
        carService.getAll().forEach(System.out::println);

        System.out.println("Changing car: ");
        teslaCar.setModel("Model S");
        carService.update(teslaCar);
        System.out.println(carService.get(teslaCar.getId()));

        System.out.println("Changing car's driver: ");
        Driver driverAlex = new Driver("Alex", "9bkwvik8");
        driverService.create(driverAlex);
        carService.addDriverToCar(driverAlex, teslaCar);
        carService.update(teslaCar);
        System.out.println(carService.get(teslaCar.getId()));

        System.out.println("Deleting car's driver: ");
        carService.removeDriverFromCar(driverKevin, kiaCar);
        System.out.println(carService.delete(kiaCar.getId()));

        System.out.println("Getting car by driver: ");
        System.out.println(carService.getAllByDriver(driverAlex.getId()));
    }
}
