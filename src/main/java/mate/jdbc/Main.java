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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer firstCar = initializeManufacturer("BMW", "Germany");
        Manufacturer secondCar = initializeManufacturer("Ferrari", "Japan");
        Manufacturer thirdCar = initializeManufacturer("Opel", "USA");
        Manufacturer fourthCar = initializeManufacturer("Kamaz", "Russian");
        manufacturerService.create(firstCar);
        manufacturerService.create(secondCar);
        manufacturerService.create(thirdCar);
        manufacturerService.create(fourthCar);
        System.out.println("Create our manufacturers. List of all cars : "
                + manufacturerService.getAll());

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driverFirst = initializeDriver("Petro","5544");
        Driver driverSecond = initializeDriver("Olga","0987");
        Driver driverThird = initializeDriver("Bogdan","7777");
        driverService.create(driverFirst);
        driverService.create(driverSecond);
        driverService.create(driverThird);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverFirst);
        drivers.add(driverSecond);
        drivers.add(driverThird);
        System.out.println("Create our drivers. List of all drivers : "
                + driverService.getAll());

        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car = initializeCar("topClass",firstCar, drivers);
        carService.create(car);
        System.out.println("Create out cars. List of cars : "
                + carService.getAll());
        car.setManufacturer(secondCar);
        carService.update(car);
        System.out.println("Update car. List of cars : "
                + carService.getAll());
        carService.delete(car.getId());
        System.out.println("List of cars after deletion");
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
