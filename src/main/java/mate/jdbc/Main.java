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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Tesla Inc.", "USA");
        manufacturer = manufacturerService.create(manufacturer);
        Driver firstDriver = new Driver("William Carrington", "345");
        Driver secondDriver = new Driver("John Bourbon", "346");
        driverService.create(firstDriver);
        driverService.create(secondDriver);

        Car car = new Car();
        car.setModel("Tesla X");
        car.setManufacturer(manufacturer);
        car.setDrivers(driverService.getAll());
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        Driver driver = new Driver("Alex Gray", "111");
        driver = driverService.create(driver);
        car.setModel("Tesla Model S");
        car.setManufacturer(manufacturerService.get(manufacturer.getId()));
        car.setDrivers(List.of(driverService.get(driver.getId())));

        System.out.println(carService.update(car));
    }
}
