package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.Collections;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Test manufacturer");
        manufacturer.setCountry("USA");
        manufacturerService.create(manufacturer);

        Driver driver = new Driver();
        driver.setName("Vova");
        driver.setLicenseNumber("234-456-634");
        driverService.create(driver);

        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("Chev");
        car.setId(1L);
        carService.create(car);
        carService.getAll().forEach(c -> System.out.println(c.getModel()));

        carService.addDriverFromCar(driver, car);
        carService.getAllByDriver(1L).forEach(c -> System.out.println(c.getModel()));

        car.setModel("Ford");
        carService.getAll().forEach(c -> System.out.println(c.getModel()));
        carService.getAllByDriver(1L).forEach(c -> System.out.println(c.getModel()));
        car.setDrivers(Collections.emptyList());
        carService.update(car);

        carService.getAllByDriver(1L).forEach(c -> System.out.println(c.getModel()));
        carService.getAllByDriver(1L).forEach(c -> System.out.println(c.getManufacturer().getName()));

        System.out.println();

        carService.getAll().forEach(c -> System.out.println(c.getModel()));

        System.out.println();

    }
}
