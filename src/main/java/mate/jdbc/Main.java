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
        manufacturerService.get(52L);
        System.out.println(manufacturer);
        manufacturerService.delete(28L);
        manufacturerService.getAll().forEach(System.out::println);
        manufacturer.setId(90L);
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
        driverService.get(47L);
        driverService.delete(40L);
        driverService.getAll().forEach(System.out::println);
        driver.setId(62L);
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
        car.setManufacturer(manufacturerService.get(42L));
        CarService carService = (CarService) serviceInjector.getInstance(CarService.class);
        car.setDrivers(drivers);
        carService.create(car);
        car.setModel("X5");
        car.setManufacturer(manufacturerService.get(3L));
        car.setDrivers(anotherDrivers);
        carService.create(car);
        car.setModel("Veyron");
        car.setManufacturer(manufacturerService.get(4L));
        System.out.println(carService.get(26L));
        carService.getAll().forEach(System.out::println);
        drivers.clear();
        drivers.add(driverService.get(89L));
        car.setDrivers(drivers);
        car.setModel("320ix");
        car.setId(34L);
        carService.update(car);
        System.out.println(carService.get(24L));
        carService.delete(23L);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(driverService.get(73L), carService.get(31L));
        System.out.println(carService.get(31L));
        carService.removeDriverFromCar(driverService.get(73L), carService.get(31L));
        System.out.println(carService.get(31L));
        carService.getAllByDriver(73L).forEach(System.out::println);
    }
}
