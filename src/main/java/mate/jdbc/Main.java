package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);

        Car bwm = new Car();
        bwm.setName("BMW Super");
        bwm.setManufacturer(manufacturerService.get(1L));
        List<Driver> bmwDrivers = new ArrayList<>();
        bmwDrivers.add(driverService.get(1L));
        bmwDrivers.add(driverService.get(2L));
        bwm.setDrivers(bmwDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(bwm);

        Car toyota = new Car();
        toyota.setName("Toyota model one");
        toyota.setManufacturer(manufacturerService.get(2L));
        List<Driver> toyotaDrivers = new ArrayList<>();
        toyotaDrivers.add(driverService.get(1L));
        toyotaDrivers.add(driverService.get(2L));
        toyota.setDrivers(toyotaDrivers);
        carService.create(toyota);

        System.out.println(carService.get(3L));
        carService.delete(4L);
        System.out.println(carService.getAll());
    }
}
