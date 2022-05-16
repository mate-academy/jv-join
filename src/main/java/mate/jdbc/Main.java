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
        Manufacturer manufacturer = new Manufacturer("Daewoo","South Korea");
        Car car = new Car("Lanos", manufacturer);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);
        car.setManufacturer(createdManufacturer);
        List<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver("Bob","12345");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        drivers.add(driver);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.addDriverToCar(driver, car);
    }
}
