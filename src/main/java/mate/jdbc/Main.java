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
        // test your code here
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Denmark");
        manufacturer.setName("MAN");
        manufacturer = manufacturerService.create(manufacturer);
        Driver driver = new Driver();
        driver.setLicenseNumber("000000");
        driver.setName("John");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driver = driverService.create(driver);
        Car car = new Car();
        car.setModel("S9999");
        car.setDrivers(List.of(driver));
        car.setManufacturer(manufacturer);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println("car = " + car);
        System.out.println("----------------------");

        Car carFromDB = carService.get(car.getId());
        System.out.println(carFromDB);
        System.out.println("----------------------");

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println("----------------------");

        Car updateCar = carFromDB;
        updateCar.setModel("NEWMAN");
        driver.setName("Pavel");
        driver.setLicenseNumber("1111111");
        driver = driverService.create(driver);
        List<Driver> updateCarDrivers = updateCar.getDrivers();
        updateCarDrivers.add(driver);
        System.out.println(carService.update(updateCar));
        System.out.println("----------------------");

        List<Car> driverCars = carService.getAllByDriver(driver.getId());
        driverCars.forEach(System.out::println);
        System.out.println("----------------------");

        Driver addedDriver = new Driver();
        addedDriver.setName("Evgen");
        addedDriver.setLicenseNumber("222222");
        addedDriver = driverService.create(addedDriver);
        carService.addDriverToCar(addedDriver,carFromDB);
        System.out.println(carService.get(carFromDB.getId()));
        System.out.println("----------------------");

        carService.removeDriverFromCar(addedDriver,carFromDB);
        System.out.println(carService.get(carFromDB.getId()));
        System.out.println("----------------------");

        System.out.println(carService.delete(car.getId()));
        System.out.println("----------------------");
    }
}
