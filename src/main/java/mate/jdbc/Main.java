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
        manufacturer.setCountry("Great Britain");
        manufacturer.setName("Land Rover");

        Driver driverFirst = new Driver();
        driverFirst.setName("Tomas");
        driverFirst.setLicenseNumber("587956");

        Driver secondDriver = new Driver();
        secondDriver.setName("Nikolas");
        secondDriver.setLicenseNumber("56789");

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverFirst);
        drivers.add(secondDriver);

        Car car = new Car();
        car.setName("Range Rover");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);
        manufacturerService.get(manufacturer.getId());
        manufacturerService.delete(manufacturer.getId());
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverFirst);
        driverService.create(secondDriver);
        driverService.get(driverFirst.getId());//  driverService.delete(driver.getId());
        driverService.update(driverFirst);
        driverService.update(secondDriver);
        driverService.getAll().forEach(System.out::println);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        carService.get(car.getId());
        carService.update(car);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driverFirst, car);
        carService.getAllByDriver(car.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(driverFirst, car);
        carService.getAllByDriver(car.getId()).forEach(System.out::println);
    }
}
