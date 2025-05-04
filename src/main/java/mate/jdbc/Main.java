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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.getAll().forEach(System.out::println);
        System.out.println();

        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Alfa Romeo");
        manufacturer.setCountry("Italy");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver();
        driver1.setName("Hope");
        driver1.setLicenseNumber("1634");
        driverService.create(driver1);

        Driver driver2 = new Driver();
        driver2.setName("Fin");
        driver2.setLicenseNumber("2987");
        driverService.create(driver2);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);

        Car car = new Car();
        car.setModel("model5");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        carService.create(car);

        carService.getAll().forEach(System.out::println);
        System.out.println();
        carService.removeDriverFromCar(driver2, car);
        carService.getAll().forEach(System.out::println);

    }
}
