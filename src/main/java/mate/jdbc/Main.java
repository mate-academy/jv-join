package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(12L);
        manufacturer.setName("Opel");
        manufacturer.setCountry("Germany");

        Driver driver = new Driver();
        driver.setId(21L);
        driver.setName("Bob");
        driver.setLicenseNumber("SDB805679");
        //DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        //driverService.create(driver);

        Driver driver1 = new Driver();
        driver1.setId(22L);
        driver1.setName("Alice");
        driver1.setLicenseNumber("DFC789245");
        //driverService.create(driver1);

        Driver driver2 = new Driver();
        driver2.setId(23L);
        driver2.setName("John");
        driver2.setLicenseNumber("DHR156248");

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver1);
        drivers.add(driver2);

        Car car = new Car();
        car.setId(8L);
        car.setModel("Vectra C");
        car.setManufacturer(manufacturer);
        car.setAllDriverForCar(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.update(car);
        System.out.println(carService.getAll());
    }
}
