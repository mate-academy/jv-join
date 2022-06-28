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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver driver = new Driver();
        driver.setName("Bob");
        driver.setLicenseNumber("12345");
        driverService.create(driver);
        System.out.println(driverService.get(23L));

        driver.setName("Bil");
        driver.setLicenseNumber("12346");
        driverService.create(driver);

        driver.setId(2L);
        driver.setName("Alice");
        driver.setLicenseNumber("12347");
        driverService.create(driver);
        System.out.println(driverService.update(driver));

        driverService.getAll().forEach(System.out::println);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();

        manufacturer.setName("Opel");
        manufacturer.setCountry("Germany");
        manufacturerService.create(manufacturer);
        System.out.println(manufacturerService.get(59L));

        manufacturer.setName("Reno");
        manufacturer.setCountry("France");
        manufacturerService.create(manufacturer);

        manufacturer.setName("Mazda");
        manufacturer.setCountry("Japan");
        manufacturerService.create(manufacturer);
        System.out.println(manufacturerService.delete(2L));

        manufacturer.setId(1L);
        manufacturer.setName("Nisan");
        manufacturer.setCountry("Japan");
        manufacturerService.create(manufacturer);
        System.out.println(manufacturerService.update(manufacturer));

        manufacturerService.getAll().forEach(System.out::println);

        Car car = new Car();
        car.setModel("Opel Insignia");
        car.setManufacturer(manufacturerService.get(59L));
        car.setDrivers(List.of(new Driver[]{driverService.get(19L), driverService.get(20L)}));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.get(3L));

        car.setModel("Reno Megan");
        car.setManufacturer(manufacturerService.get(61L));
        carService.create(car);

        car.setModel("Mazda rx7");
        car.setManufacturer(manufacturerService.get(63L));
        carService.create(car);
        System.out.println(carService.delete(2L));

        car.setId(3L);
        car.setModel("Nisan Leaf");
        car.setManufacturer(manufacturerService.get(64L));
        carService.create(car);
        System.out.println(carService.update(car));

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(20L).forEach(System.out::println);

        carService.addDriverToCar(driverService.get(19L), carService.get(1L));
        carService.removeDriverFromCar(driverService.get(19L), carService.get(3L));
    }
}
