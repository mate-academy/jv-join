package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver();
        driver1.setName("Joe");
        driver1.setLicenseNumber("3K424DgF");
        driverService.create(driver1);
        Driver driver2 = new Driver();
        driver2.setName("Butcher");
        driver2.setLicenseNumber("kjfwkjfFL");
        driverService.create(driver2);
        Driver driver3 = new Driver();
        driver3.setName("Hello");
        driver3.setLicenseNumber("Abracadabra");
        driverService.create(driver3);


        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Billy");
        manufacturer.setCountry("France");
        manufacturerService.create(manufacturer);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car();
        car.setModel("Ford");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driver1, driver3));

        Car car2 = new Car();
        car2.setModel("Ford");
        car2.setManufacturer(manufacturer);
        car2.setDrivers(List.of(driver1, driver2, driver3));

        carService.create(car);
        carService.create(car2);
        carService.getAllByDriver(1L).forEach(System.out::println);

    }
}
