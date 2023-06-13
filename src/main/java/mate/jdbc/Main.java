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
        Driver arthurDriver = new Driver("Arthur", "666");
        Driver madhooDriver = new Driver("Madhoo", "1933");
        {
            DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
            driverServiceTest(driverService, arthurDriver);
            driverServiceTest(driverService, madhooDriver);
            driverService.getAll().forEach(System.out::println);
        }
        List<Driver> drivers = new ArrayList<>();
        {
            drivers.add(arthurDriver);
            drivers.add(madhooDriver);
            System.out.println(drivers);
        }
        Manufacturer daewooManufacturer = new Manufacturer("Daewoo", "Mongolia");
        Manufacturer volvoManufacturer = new Manufacturer("Volvo", "OAO");
        {
            ManufacturerService manufacturerService =
                    (ManufacturerService) injector.getInstance(ManufacturerService.class);
            manufacturerServiceTest(manufacturerService, daewooManufacturer);
            manufacturerServiceTest(manufacturerService, volvoManufacturer);
            manufacturerService.getAll().forEach(System.out::println);
        }
        Car daewooMatiz = new Car("Daewoo Matiz", daewooManufacturer, drivers);
        Car volvoEx90 = new Car("Volvo EX90", volvoManufacturer, drivers);
        {
            CarService carService = (CarService) injector.getInstance(CarService.class);
            carServiceTest(carService, daewooMatiz);
            carServiceTest(carService, volvoEx90);
            carService.getAll().forEach(System.out::println);
        }
    }

    private static void carServiceTest(CarService carService, Car car) {
        carService.create(car);
        carService.update(car);
    }

    private static void manufacturerServiceTest(
            ManufacturerService manufacturerService, Manufacturer manufacturer) {
        manufacturerService.create(manufacturer);
        manufacturerService.update(manufacturer);
    }

    private static void driverServiceTest(DriverService driverService, Driver driver) {
        driverService.create(driver);
        driverService.update(driver);
    }
}
