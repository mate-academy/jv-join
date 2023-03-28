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
    private static final List<Driver> DRIVERS_1 = new LinkedList<>();
    private static final List<Driver> DRIVERS_2 = new LinkedList<>();

    public static void main(String[] args) {
        Driver driver1 = driverService
                .create(new Driver("Ivan", "456654"));
        Driver driver2 = driverService.create(new Driver("Masha", "6548123"));
        Driver driver3 = driverService.create(new Driver("Misha", "6548813"));
        DRIVERS_1.add(driver1);
        DRIVERS_1.add(driver2);
        DRIVERS_1.add(driver3);
        Driver driver4 = driverService.create(new Driver("Alyona", "523945723"));
        Driver driver5 = driverService.create(new Driver("Artur", "12341234"));
        DRIVERS_2.add(driver4);
        DRIVERS_2.add(driver5);
        Manufacturer honda = manufacturerService
                .create(new Manufacturer("Honda", "Japan"));
        Manufacturer mercedes = manufacturerService
                .create(new Manufacturer("Mercedes", "Germany"));
        Car car1 = carService.create(new Car("Civic", honda, DRIVERS_1));
        Car car2 = carService.create(new Car("E-Class", mercedes, DRIVERS_2));
        System.out.println(carService.get(car1.getId()));
        System.out.println(carService.get(car2.getId()));
        carService.removeDriver(driver2, car1);
        Driver driver6 = driverService.create(new Driver("Vasyl", "4524397523"));
        carService.addDriver(driver6, car2);
        System.out.println(carService.getAll());
        carService.addDriver(driver6, car1);
        System.out.println(carService.getAllByDriver(driver6));
        carService.delete(car1.getId());
        System.out.println(carService.getAll());
    }
}
