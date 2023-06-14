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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturerPeugeot = new Manufacturer("Peugeot","France");
        manufacturerService.create(manufacturerPeugeot);
        Manufacturer manufacturerVolkswagen = new Manufacturer("Volkswagen", "German");
        manufacturerService.create(manufacturerVolkswagen);
        Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");
        manufacturerService.create(manufacturerFord);

        Driver driverLeo = new Driver("Leo", "12-23-42");
        driverService.create(driverLeo);
        Driver driverDave = new Driver("Dave", "22-33-44");
        driverService.create(driverDave);
        Driver driverJohn = new Driver("John", "15-25-35");
        driverService.create(driverJohn);

        Car mustangCar = new Car("Mustang", manufacturerFord);
        carService.create(mustangCar);
        Car boxerCar = new Car("Boxer", manufacturerPeugeot);
        carService.create(boxerCar);
        Car golfCar = new Car("Golf", manufacturerVolkswagen);
        carService.create(golfCar);

        carService.getAll().forEach(System.out::println);
        ArrayList<Driver> drivers = new ArrayList<>(List.of(driverJohn, driverDave, driverLeo));
        mustangCar.setDrivers(drivers);
        carService.update(mustangCar);
        carService.delete(boxerCar.getId());
        carService.removeDriverFromCar(driverJohn, mustangCar);
        golfCar.setDrivers(new ArrayList<>(List.of(driverLeo)));
        carService.update(golfCar);
        carService.addDriverToCar(driverJohn, golfCar);
        carService.getAllByDriver(driverDave.getId()).forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
    }
}
