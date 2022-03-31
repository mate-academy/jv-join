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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Driver oleg = driverService.get(1L);
        Driver ann = driverService.get(2L);
        Driver kate = driverService.get(3L);
        Driver mark = driverService.get(5L);
        Manufacturer volvo = manufacturerService.get(1L);
        Manufacturer mercedes = manufacturerService.get(2L);
        Manufacturer bmw = manufacturerService.get(3L);
        Car x3 = carService.get(1L);
        Car x5 = carService.get(3L);
        Car xc90 = carService.get(4L);
        Car c400 = carService.get(5L);
        carService.removeDriverFromCar(kate,c400);
        List<Driver> drivers = c400.getDrivers();
        for (Driver driver : drivers) {
            System.out.println(driver);
        }
    }
}
