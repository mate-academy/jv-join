package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.List;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static ManufacturerService manufacturerService = (ManufacturerService) injector
            .getInstance(ManufacturerService.class);
    private static DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static CarService carService = (CarService) injector
            .getInstance(CarService.class);

    public static void main(String[] args) {
        System.out.println("Original data");
        printDriversManufacturers();
        showCars();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Tesla");
        manufacturer.setCountry("USA");
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setCountry("Japan");
        manufacturer1.setName("Suzuki");
        Driver driver = new Driver();
        driver.setName("Ivan");
        driver.setLicenseNumber("12345678");
        Driver driver1 = new Driver();
        driver1.setName("Leonid");
        driver1.setLicenseNumber("87654321");
        addManufacturerDriver(manufacturer, driver);
        addManufacturerDriver(manufacturer1, driver1);
        Car car = new Car("model X", manufacturer, List.of(driver, driver1));
        addCar(car);
        System.out.println("After adding");
        printDriversManufacturers();
        showCars();
        System.out.println("Removing driver: " + driver + " from car: " + car);
        carService.removeDriverFromCar(driver, car);
        System.out.println("After removing");
        showCars();
        showCarsByDriver(driver);
        showCarsByDriver(driver1);
        System.out.println("Adding driver: " + driver + " to car: " + car);
        carService.addDriverToCar(driver, car);
        System.out.println("After adding");
        showCars();
        Car newCar = new Car("swift", manufacturer1, List.of());
        newCar.setId(car.getId());
        System.out.println("Updating car: " + car + " by car: " + newCar);
        carService.update(newCar);
        System.out.println("After updating");
        showCars();
        System.out.println("Removing car: " + car);
        carService.delete(car.getId());
        System.out.println("After removing");
        showCars();
    }

    private static void showCarsByDriver(Driver driver) {
        System.out.println("Getting cars by driver: " + driver);
        List<Car> cars = carService.getAllByDriver(driver.getId());
        System.out.println("Cars: " + cars);
    }

    private static void addCar(Car car) {
        car = carService.create(car);
        System.out.println("Added: " + car);

    }

    private static void showCars() {
        List<Car> cars = carService.getAll();
        System.out.println("Cars: " + cars);
    }

    private static void addManufacturerDriver(Manufacturer manufacturer, Driver driver) {
        manufacturer = manufacturerService.create(manufacturer);
        System.out.println("Added: " + manufacturer);
        driver = driverService.create(driver);
        System.out.println("Added: " + driver);
    }

    private static void printDriversManufacturers() {
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        List<Driver> drivers = driverService.getAll();
        System.out.println("Manufacturers: " + manufacturers);
        System.out.println("Drivers: " + drivers);
    }
}
