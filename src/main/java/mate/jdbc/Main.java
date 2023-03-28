package mate.jdbc;

import java.util.LinkedList;
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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final Driver DRIVER_1 = driverService
            .create(new Driver("Ivan", "456654"));
    private static final Driver DRIVER_2 = driverService.create(new Driver("Masha", "6548123"));
    private static final Driver DRIVER_3 = driverService.create(new Driver("Misha", "6548813"));
    private static final List<Driver> DRIVERS_1 = new LinkedList<>();
    private static final Driver DRIVER_4 = driverService.create(new Driver("Alyona", "523945723"));
    private static final Driver DRIVER_5 = driverService.create(new Driver("Artur", "12341234"));
    private static final Driver DRIVER_6 = driverService.create(new Driver("Vasyl", "4524397523"));
    private static final List<Driver> DRIVERS_2 = new LinkedList<>();
    private static final Manufacturer HONDA = manufacturerService
            .create(new Manufacturer("Honda", "Japan"));
    private static final Manufacturer MERCEDES = manufacturerService
            .create(new Manufacturer("Mercedes", "Germany"));

    public static void main(String[] args) {
        DRIVERS_1.add(DRIVER_1);
        DRIVERS_1.add(DRIVER_2);
        DRIVERS_1.add(DRIVER_3);
        DRIVERS_2.add(DRIVER_4);
        DRIVERS_2.add(DRIVER_5);
        Car car1 = carService.create(new Car("Civic", HONDA, DRIVERS_1));
        Car car2 = carService.create(new Car("E-Class", MERCEDES, DRIVERS_2));
        System.out.println(carService.get(car1.getId()));
        System.out.println(carService.get(car2.getId()));
        carService.removeDriver(DRIVER_2, car1);
        carService.addDriver(DRIVER_6, car2);
        System.out.println(carService.getAll());
        carService.addDriver(DRIVER_6, car1);
        System.out.println(carService.getAllByDriver(DRIVER_6));
        carService.delete(car1.getId());
        System.out.println(carService.getAll());
    }
}
