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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Bob");
        driver.setLicenseNumber("11111");
        System.out.println(driverService.create(driver));
        System.out.println(driverService.get(2L));
        System.out.println(driverService.getAll());
        System.out.println(driverService.update(driver));
        System.out.println(driverService.delete(1L));
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Honda");
        manufacturer.setCountry("Japan");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println(manufacturerService.create(manufacturer));
        System.out.println(manufacturerService.get(1L));
        System.out.println(manufacturerService.getAll());
        System.out.println(manufacturerService.update(manufacturer));
        System.out.println(manufacturerService.delete(30L));
        Car car = new Car();
        car.setModel("Jazz");
        car.setManufacturer(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        System.out.println(carService.get(1L));
        System.out.println(carService.getAll());
        System.out.println(carService.update(car));
        System.out.println(carService.delete(2L));
        System.out.println(carService.getAllByDriver(2L));
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);

    }
}
