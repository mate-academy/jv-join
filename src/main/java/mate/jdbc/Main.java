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
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Mercedes");
        manufacturer.setCountry("Germany");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Yuliya");
        driver.setLicenseNumber("Sen19");
        driverService.create(driver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        Car car = new Car();
        car.setModel("AMG");
        car.setDrivers(drivers);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        car.setManufacturer(manufacturerService.get(33L));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        System.out.println(carService.get(33L));
        driver.setName("Bob");
        driver.setLicenseNumber("Bob20");
        driverService.create(driver);
        Driver secondDriver = new Driver();
        Car newCar = carService.create(car);
        carService.addDriverToCar(secondDriver, newCar);
        System.out.println(carService.getAll());
        carService.removeDriverFromCar(driver, newCar);
        System.out.println(carService.getAll());
        newCar.setModel("AMG_GT_63");
        System.out.println(carService.update(car));
        System.out.println(carService.delete(car.getId()));
        System.out.println(carService.getAllByDriver(30L));

    }
}
