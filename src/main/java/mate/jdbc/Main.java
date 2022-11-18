package mate.jdbc;

import java.util.ArrayList;
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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver("Jack", "221");
        driverService.create(driver);
        System.out.println(driverService.get(driver.getId()));
        driverService.getAll().stream().forEach(System.out::println);
        driver.setLicenseNumber("322");
        driverService.update(driver);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Ferrari", "Germany");
        manufacturerService.create(manufacturer);
        System.out.println(manufacturerService.get(manufacturer.getId()));
        manufacturerService.getAll().stream().forEach(System.out::println);
        manufacturer.setCountry("Italy");
        manufacturerService.update(manufacturer);
        driverService.delete((long) 1);
        manufacturerService.delete((long) 1);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car car = new Car("someModel", manufacturer, new ArrayList());
        car.getDrivers().add(driver);
        carService.create(car);
        carService.get(car.getId());
        carService.getAll().stream().forEach(System.out::println);
        carService.getAllByDriver(driver.getId()).stream().forEach(System.out::println);
        car.setModel("anotherModel");
        System.out.println(carService.update(car));
        Driver newDriver = new Driver("Piter", "2222");
        driverService.create(newDriver);
        carService.addDriverToCar(newDriver, car);
        carService.removeDriverFromCar(driver, car);
        carService.delete(car.getId());
    }
}
