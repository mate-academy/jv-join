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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Ferrari", "Italy");
        manufacturerService.create(manufacturer);
        Driver driver = new Driver("Robert", "1q2w3e4r");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        Car car = new Car("SF90 Stradale", manufacturer, drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        carService.addDriverToCar(driver, car);
        System.out.println(carService.get(car.getId()));
        carService.getAllByDriver(driver.getId()).forEach(System.out::println);
        car.setModel("Ferrari Enzo");
        System.out.println(carService.update(car));
        carService.removeDriverFromCar(driver, car);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(car.getId()));
    }
}
