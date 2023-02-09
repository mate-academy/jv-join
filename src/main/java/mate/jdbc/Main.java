package mate.jdbc;

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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Great Britain");
        manufacturer.setName("Land Rover");
        manufacturerService.create(manufacturer);
        manufacturerService.get(manufacturer.getId());
        manufacturerService.delete(manufacturer.getId());
        manufacturerService.getAll().forEach(System.out::println);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car();
        car.setName("Range Rover");
        car.setManufacturer(manufacturer.getName());
        carService.create(car);
        carService.get(car.getId());
        carService.delete(car.getId());
        carService.update(car);
        carService.getAll().forEach(System.out::println);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Nikolas");
        driver.setLicenseNumber("56789");
        driverService.create(driver);
        driverService.get(driver.getId());//  driverService.delete(driver.getId());
        driverService.update(driver);
        driverService.getAll().forEach(System.out::println);

        carService.addDriverToCar(driver, car);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(driver, car);
        carService.getAllByDriver(driver.getId()).forEach(System.out::println);
    }
}
