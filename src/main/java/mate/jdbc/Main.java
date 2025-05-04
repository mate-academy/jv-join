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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Germany");
        manufacturer.setName("BMW");
        manufacturer = manufacturerService.create(manufacturer);

        Driver driver = new Driver();
        driver.setLicenseNumber("123456789");
        driver.setName("Bob");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driver = driverService.create(driver);

        Car car = new Car();
        car.setModel("x5");
        List<Driver> drivers = new ArrayList<>();
        car.setDrivers(drivers);
        car.setManufacturer(manufacturer);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        System.out.println("Create: ");
        System.out.println(carService.create(car));
        System.out.println("Get: ");
        System.out.println(carService.get(car.getId()));

        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        System.out.println("Update: ");
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()));
    }
}

