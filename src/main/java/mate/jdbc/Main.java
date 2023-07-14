package mate.jdbc;

import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.impl.CarServiceImpl;
import mate.jdbc.service.impl.DriverServiceImpl;
import mate.jdbc.service.impl.ManufacturerServiceImpl;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        CarService carService = new CarServiceImpl();
        DriverService driverService = new DriverServiceImpl();
        List<Driver> all1 = driverService.getAll();
        ManufacturerService manufacturerService = new ManufacturerServiceImpl();
        manufacturerService.getAll();
        Driver driver1 = driverService.get(11L);
        Car car = carService.get(3L);

        System.out.println(carService.getAll().toString());
        System.out.println(car.toString());
        System.out.println(driver1.toString());
    }
}
