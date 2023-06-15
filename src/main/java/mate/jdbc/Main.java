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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        final Car car = new Car();
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("volvo");
        manufacturer.setCountry("usa");
        manufacturerService.create(manufacturer);
        car.setManufacturer(manufacturer);
        car.setModel("new car");
        Driver driver = new Driver();
        driver.setName("kolyan");
        driver.setLicenseNumber("909076");
        driverService.create(driver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        car.setDrivers(drivers);
        carService.create(car);

        System.out.println(carService.get(car.getId()));
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(driver.getId()));
        car.setModel("flying car");
        System.out.println("update successful: " + carService.update(car));
        System.out.println("deletion complete: " + carService.delete(car.getId()));

        Driver petya = new Driver();
        petya.setName("petya");
        petya.setLicenseNumber("101010");
        carService.addDriverToCar(petya, car);
        System.out.println("check if new driver was added: " + car.getDrivers().contains(petya));
        carService.removeDriverFromCar(petya, car);
        System.out.println(
                "check if new driver was removed: " + !car.getDrivers().contains(petya));

    }
}
