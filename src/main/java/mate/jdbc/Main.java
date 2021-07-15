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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Toyota");
        manufacturer.setCountry("Japan");
        manufacturer.setId(17L);
        Driver mark = new Driver();
        mark.setName("Mark");
        mark.setLicenseNumber("0001");
        mark.setId(20L);
        mark.setId(17L);
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("0002");
        bob.setId(19L);
        bob.setId(18L);
        List<Driver> drivers = List.of(mark, bob);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        car.setName("Supra");
        car.setId(8L);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.update(car);
        System.out.println(carService.get(6L));
        carService.getAll().stream().forEach(System.out::println);
        carService.delete(6L);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(20L);
        System.out.println(carService.getAllByDriver(driver));
    }
}
