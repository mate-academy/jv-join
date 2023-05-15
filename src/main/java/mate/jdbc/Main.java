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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = manufacturerService.get(1L);
        Car car = new Car();
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(1L));
        car.setId(1L);
        car.setManufacturer(manufacturer);
        car.setModel("BMW X10");
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        carService.delete(2L);
        Manufacturer manufacturerUpdate = manufacturerService.get(2L);
        Car carUpdate = new Car(1L, manufacturerUpdate, "M4 Competition", drivers);
        carService.getAll().forEach(System.out::println);
        System.out.println("Car after update --------------");
        carService.update(carUpdate);
        System.out.println(carService.get(carUpdate.getId()));
        Driver driver = driverService.get(2L);
        carService.addDriverToCar(driver, car);
        System.out.println("Car after adding new driver --------------");
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(driver, car);
        System.out.println("Car after removed driver --------------");
        System.out.println(carService.get(car.getId()));
    }
}
