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
        Car car = new Car();
        car.setModel("Accord");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Honda");
        manufacturer.setCountry("Ukraine");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);
        car.setManufacturer(createdManufacturer);
        List<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver();
        driver.setLicenseNumber("123");
        driver.setName("Bob");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver createdDriver = driverService.create(driver);
        drivers.add(createdDriver);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car createdCar = carService.create(car);
        carService.get(createdCar.getId());
        carService.getAll();
        driver = new Driver();
        driver.setLicenseNumber("123");
        driver.setName("Bob");
        createdDriver = driverService.create(driver);
        carService.addDriverToCar(createdDriver, createdCar);
        carService.removeDriverFromCar(createdDriver, createdCar);
        car.getDrivers().add(createdDriver);
        carService.update(createdCar);
        carService.delete(createdCar.getId());
    }
}
