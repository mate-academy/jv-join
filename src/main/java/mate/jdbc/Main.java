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
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        // Driver d1 = new Driver(null, "driver4", "ln4");
        // Manufacturer m1 = new Manufacturer(null, "manufacturer4", "country4");

        // m1 = manufacturerService.create(m1);
        // d1 = driverService.create(d1);
        Driver d2 = driverService.get(2L);
        // List<Driver> drivers = new ArrayList<>();
        // drivers.add(d1);
        // drivers.add(d2);
        Car c1 = carService.get(4L);
        System.out.println(c1);
        Driver d3 = driverService.get(3L);
        carService.addDriverToCar(d3, c1);
        carService.removeDriverFromCar(d2, c1);
        c1 = carService.get(4L);
        System.out.println(c1);
    }
}
