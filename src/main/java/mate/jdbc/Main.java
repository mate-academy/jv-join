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
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        Manufacturer toyota = new Manufacturer("TOYOTA", "JAPAN");
        Manufacturer volvo = new Manufacturer("VOLVO", "SWEDEN");
        Driver driverBob = new Driver("Bob", "8410");
        Driver driverAlice = new Driver("Alice", "3494");
        Driver driverSteve = new Driver("Steve", "ULTIMATE_ULTRA_MEGA_777777");
        Car prius = new Car("Prius", toyota, List.of(driverBob, driverSteve));
        Car xc90 = new Car("XC90", volvo, List.of(driverAlice));

        System.out.println(manufacturerService.create(toyota));
        System.out.println(manufacturerService.create(volvo));
        System.out.println(driverService.create(driverBob));
        System.out.println(driverService.create(driverAlice));
        System.out.println(driverService.create(driverSteve));
        System.out.println(carService.create(prius));
        System.out.println(carService.create(xc90));

        carService.addDriverToCar(driverSteve, xc90);
        carService.removeDriverFromCar(driverAlice, xc90);
        System.out.println(carService.getAllByDriver(driverSteve.getId()));
        xc90.setModel("EX90");
        carService.update(xc90);
        System.out.println(carService.get(xc90.getId()));
        System.out.println(carService.delete(prius.getId()));
        carService.getAll().forEach(System.out::println);
    }
}
