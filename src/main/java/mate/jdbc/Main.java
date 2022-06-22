package mate.jdbc;

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
        manufacturer.setName("Nissan");
        manufacturer.setCountry("Japan");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);

        Driver driver = new Driver();
        driver.setName("Jake");
        driver.setLicenseNumber("AE4576478");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driver);

        driver.setName("Lora");
        driver.setLicenseNumber("AE8645789");
        driverService.create(driver);

        driver.setName("Alice");
        driver.setLicenseNumber("754");
        driverService.create(driver);

        List<Driver> drivers = driverService.getAll();

        Car car = new Car();
        car.setModel("Skyline");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        List<Car> allCars = carService.getAll();
        allCars.forEach(System.out::println);

        List<Car> allByDriver = carService.getAllByDriver(driver.getId());
        allByDriver.forEach(System.out::println);

        System.out.println(carService.get(car.getId()));

        car = carService.get(1L);
        drivers = car.getDrivers();
        drivers.get(0).setName("Joni D");
        driverService.update(drivers.get(0));
        carService.update(car);

        allCars = carService.getAll();
        allCars.forEach(System.out::println);

        Driver newDriver = new Driver();
        newDriver.setName("Christopher L");
        newDriver.setLicenseNumber("ER567MN");
        driverService.create(newDriver);
        carService.addDriverToCar(newDriver, car);
        allCars = carService.getAll();
        allCars.forEach(System.out::println);

        Driver removeDriver = carService.get(1L).getDrivers().get(1);
        carService.removeDriverFromCar(removeDriver, car);
        allCars = carService.getAll();
        allCars.forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
    }
}
