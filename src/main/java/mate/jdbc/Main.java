package mate.jdbc;

import java.util.*;
import mate.jdbc.lib.*;
import mate.jdbc.model.*;
import mate.jdbc.service.*;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("volvo");
        manufacturer.setCountry("usa");
        manufacturerService.create(manufacturer);
        car.setManufacturer(manufacturer);
        car.setModel("new car");
        Driver driver = new Driver();
        driver.setName("kolyan");
        driver.setLicenseNumber("909076");
        driverService.create(driver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        car.setDrivers(drivers);
        carService.create(car);

//        System.out.println(carService.get(car.getId()));
//        carService.getAll().forEach(System.out::println);
    }
}
