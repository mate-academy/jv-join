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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer("BMW", "German");
        manufacturer = manufacturerService.create(manufacturer);
        Driver driver = new Driver("Vito", "32532");
        driver = driverService.create(driver);
        List<Driver> driversList = new ArrayList<>();
        driversList.add(driver);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("Q8", manufacturer);
        car.setDrivers(driversList);
        carService.create(car);
    }
}
