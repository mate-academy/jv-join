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
        DriverService driverService = 
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> driversList = new ArrayList<>();
        driversList.add(driverService.get(2L));
        driversList.add(driverService.get(4L));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("Test method getAllByDriver:");
        System.out.println(carService.getAllByDriver(2L));
        ManufacturerService manufacturerService = 
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Car testCar = new Car();
        testCar.setModel("XJ 3.0 V6");
        testCar.setManufacturer(manufacturerService.get(9L));
        testCar.setDrivers(driversList);
        carService.create(testCar);
        testCar.setId(5L);
        testCar.setModel("XK150");
        carService.update(testCar);
        System.out.println("Test method get:");
        System.out.println(carService.get(5L));
        carService.addDriverToCar(driversList.get(0), testCar);
        carService.addDriverToCar(driversList.get(1), testCar);
        testCar.setId(3L);
        carService.removeDriverFromCar(driverService.get(2L), testCar);
        carService.delete(3L);
        System.out.println("Test method getAll:");
        System.out.println(carService.getAll());
    }
}
