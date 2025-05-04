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
        // test your code here
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("BMV");
        manufacturer1.setCountry("Germany");
        manufacturerService.create(manufacturer1);
        Manufacturer manufacturer2 = new Manufacturer();
        manufacturer2.setName("Ford");
        manufacturer2.setCountry("USA");
        manufacturerService.create(manufacturer1);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver();
        driver1.setName("Vasya");
        driver1.setLicenseNumber("12345");
        driverService.create(driver1);
        Driver driver2 = new Driver();
        driver2.setName("Petya");
        driver2.setLicenseNumber("56789");
        driverService.create(driver2);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);
        Car car1 = new Car();
        car1.setModel("X5");
        car1.setDrivers(drivers);
        car1.setManufacturer(manufacturer1);
        manufacturerService.create(manufacturer1);
        Car car2 = new Car();
        car2.setModel("Focus");
        car2.setDrivers(drivers);
        car2.setManufacturer(manufacturer2);
        manufacturerService.create(manufacturer2);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("------create------");
        System.out.println(carService.create(car1));
        System.out.println(carService.create(car2));
        System.out.println("------get------");
        System.out.println(carService.get(car1.getId()));
        System.out.println(carService.get(car2.getId()));
        System.out.println("------getAll------");
        System.out.println(carService.getAll());
        System.out.println("------update------");
        car1.setModel("Outlander");
        manufacturer1.setName("Mitsubishi");
        manufacturer1.setCountry("Japan");
        car1.setManufacturer(manufacturer1);
        manufacturerService.update(manufacturer1);
        System.out.println(carService.update(car1));
        System.out.println("------delete------");
        System.out.println(carService.delete(car2.getId()));
        System.out.println("------addDriverToCar------");
        carService.addDriverToCar(driver1, car1);
        System.out.println(carService.get(car1.getId()));
        System.out.println("------getAllByDriver------");
        carService.getAllByDriver(driver1.getId()).forEach(System.out::println);
        System.out.println("------removeDriverFromCar------");
        carService.removeDriverFromCar(driver1, car1);
        System.out.println("------getAll------");
        carService.getAll().forEach(System.out::println);;
    }
}
