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
    private static final String INJECTOR_INSTANCE = "mate.jdbc";

    public static void main(String[] args) {
        Injector serviceInjector = Injector.getInstance(INJECTOR_INSTANCE);
        ManufacturerService manufacturerService = (ManufacturerService) serviceInjector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Ford");
        manufacturer.setCountry("USA");
        manufacturerService.create(manufacturer);
        manufacturer.setName("Tata");
        manufacturer.setCountry("India");
        manufacturerService.create(manufacturer);
        manufacturer.setName("BMW");
        manufacturer.setCountry("Germany");
        manufacturerService.create(manufacturer);
        manufacturerService.get(1L);
        System.out.println(manufacturer);
        manufacturerService.delete(2L);
        manufacturerService.getAll().forEach(System.out::println);
        manufacturer.setId(3L);
        manufacturer.setName("Bugatti");
        manufacturer.setCountry("Italy");
        manufacturerService.update(manufacturer);
        manufacturerService.getAll().forEach(System.out::println);
        DriverService driverService = (DriverService) serviceInjector
                .getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Igor");
        driver.setLicenseNumber("UA9812S04");
        List<Driver> drivers = new ArrayList<>();
        List<Driver> anotherDrivers = new ArrayList<>();
        driverService.create(driver);
        drivers.add(driver);
        anotherDrivers.add(driver);
        driver.setName("Kolya");
        driver.setLicenseNumber("UA7712S09");
        driverService.create(driver);
        drivers.add(driver);
        anotherDrivers.add(driver);
        driver.setName("Bohdan");
        driver.setLicenseNumber("UA3908S03");
        driverService.create(driver);
        drivers.add(driver);
        driverService.get(1L);
        driverService.delete(2L);
        driverService.getAll().forEach(System.out::println);
        driver.setId(3L);
        driver.setName("Gogi");
        driver.setLicenseNumber("UA3117S09");
        driverService.update(driver);
        drivers.add(driver);
        driver.setName("Alex");
        driver.setLicenseNumber("UA3593S04");
        driverService.create(driver);
        driverService.getAll().forEach(System.out::println);
        Car car = new Car();
        car.setModel("Focus");
        car.setManufacturer(manufacturerService.get(1L));
        CarService carService = (CarService) serviceInjector.getInstance(CarService.class);
        car.setDrivers(drivers);
        carService.create(car);
        car.setModel("X5");
        car.setManufacturer(manufacturerService.get(3L));
        car.setDrivers(anotherDrivers);
        carService.create(car);
        car.setModel("Veyron");
        car.setManufacturer(manufacturerService.get(4L));
        System.out.println(carService.get(4L));
        carService.getAll().forEach(System.out::println);
        drivers.clear();
        drivers.add(driverService.get(4L));
        car.setDrivers(drivers);
        car.setModel("320ix");
        car.setId(12L);
        carService.update(car);
        System.out.println(carService.get(12L));
        carService.delete(12L);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driverService.get(14L), carService.get(6L));
        System.out.println(carService.get(6L));
        carService.removeDriverFromCar(driverService.get(14L), carService.get(6L));
        System.out.println(carService.get(6L));
        carService.getAllByDriver(22L).forEach(System.out::println);
    }
}
