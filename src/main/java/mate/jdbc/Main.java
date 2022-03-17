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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer firstCar = initializeManufacturer("BMW", "Germany");
        Manufacturer secondCar = initializeManufacturer("Audi", "Germany");
        manufacturerService.create(firstCar);
        manufacturerService.create(secondCar);
        System.out.println("Create manufacturers. List of all cars: "
                + manufacturerService.getAll() + "\n");
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driverFirst = initializeDriver("Alex", "2222");
        Driver driverSecond = initializeDriver("John", "3333");
        driverService.create(driverFirst);
        driverService.create(driverSecond);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverFirst);
        drivers.add(driverSecond);
        System.out.println("Create our drivers. List of all drivers: "
                + driverService.getAll() + "\n");
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car = initializeCar("someModel", firstCar, drivers);
        carService.create(car);
        System.out.println("Create cars. List of cars: "
                + carService.getAll() + "\n");
        car.setManufacturer(secondCar);
        carService.update(car);
        System.out.println("Update car. List of cars: "
                + carService.getAll() + "\n");
        carService.delete(car.getId());
        System.out.println("List of cars after deletion" + "\n");
        carService.getAllByDriver(driverFirst.getId());
    }

    private static Car initializeCar(String model,
                                     Manufacturer manufacturer, List<Driver> drivers) {
        Car car = new Car();
        car.setModel(model);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        return car;
    }

    private static Driver initializeDriver(String name, String licenseNumber) {
        Driver driver = new Driver();
        driver.setName(name);
        driver.setLicenseNumber(licenseNumber);
        return driver;
    }

    private static Manufacturer initializeManufacturer(String name, String country) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName(name);
        manufacturer.setCountry(country);
        return manufacturer;
    }
}
