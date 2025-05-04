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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Japan");
        manufacturer.setName("Toyota");
        manufacturer = manufacturerService.create(manufacturer);
        Driver driver = new Driver();
        driver.setLicenseNumber("ZA556677");
        driver.setName("Myke");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driver = driverService.create(driver);
        Car car = new Car();
        car.setModel("BK915");
        car.setDrivers(List.of(driver));
        car.setManufacturer(manufacturer);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        Car carFromDB = carService.get(car.getId());

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        Car updateCar = carFromDB;
        updateCar.setModel("Vertigo");
        driver.setName("Oleg");
        driver.setLicenseNumber("KA54321");
        driver = driverService.create(driver);
        List<Driver> updateCarDrivers = updateCar.getDrivers();
        updateCarDrivers.add(driver);
        System.out.println(carService.update(updateCar));

        List<Car> driverCars = carService.getAllByDriver(driver.getId());
        driverCars.forEach(System.out::println);

        Driver addedDriver = new Driver();
        addedDriver.setName("Igor");
        addedDriver.setLicenseNumber("5746392");
        addedDriver = driverService.create(addedDriver);
        carService.addDriverToCar(addedDriver,carFromDB);
        System.out.println(carService.get(carFromDB.getId()));

        carService.removeDriverFromCar(addedDriver,carFromDB);
        System.out.println(carService.get(carFromDB.getId()));

        System.out.println(carService.delete(car.getId()));
    }
}
