package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final Random random = new Random();

    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer ferrari = new Manufacturer("Ferrari", "Italy");

        bmw = manufacturerService.create(bmw);
        ferrari = manufacturerService.create(ferrari);

        Driver driver1 = new Driver("driver1", generateLicenseNumber());
        Driver driver2 = new Driver("driver2", generateLicenseNumber());

        driver1 = driverService.create(driver1);
        driver2 = driverService.create(driver2);

        Car bmwE46 = new Car("E46", bmw, new ArrayList<>(List.of(driver1, driver2)));
        Car laFerrari = new Car("LaFerrari", ferrari, new ArrayList<>(List.of(driver2)));

        bmwE46 = carService.create(bmwE46);
        laFerrari = carService.create(laFerrari);

        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(driver1, laFerrari);
        carService.removeDriverFromCar(driver2, bmwE46);

        carService.getAll().forEach(System.out::println);
    }

    private static String generateLicenseNumber() {
        return Integer.toString(random.nextInt(1000000000));
    }
}
