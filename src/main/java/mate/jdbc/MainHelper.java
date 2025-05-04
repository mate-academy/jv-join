package mate.jdbc;

import java.util.List;
import java.util.UUID;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class MainHelper {
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void initializeServices(Injector injector) {
        manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        carService = (CarService) injector.getInstance(CarService.class);
    }

    public static Manufacturer createManufacturer(ManufacturerService manufacturerService,
                                                  String name, String country) {
        Manufacturer manufacturer = new Manufacturer(name, country);
        return manufacturerService.create(manufacturer);
    }

    public static Driver createDriver(DriverService driverService, String name) {
        String licenseNumber = generateLicenseNumber();
        Driver driver = new Driver(name, licenseNumber);
        return driverService.create(driver);
    }

    public static Car createCar(CarService carService,
                                String model, Manufacturer manufacturer, List<Driver> drivers) {
        Car car = new Car(model, manufacturer, drivers);
        return carService.create(car);
    }

    private static String generateLicenseNumber() {
        return UUID.randomUUID().toString();
    }
}
