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
        System.out.println("Manufacturers: ");
        Manufacturer toyota = new Manufacturer("Toyota motors", "Japan");
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        Manufacturer kia = new Manufacturer("KIA motors", "South Korea");
        System.out.println(manufacturerService.create(toyota));
        System.out.println(manufacturerService.create(mercedes));
        System.out.println(manufacturerService.create(kia));

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        System.out.println("Drivers: ");
        Driver driverJhon = new Driver("Jhon", "LICJhon");
        Driver driverAlice = new Driver("Alice", "LICAlice");
        Driver driverBob = new Driver("Bob", "LICBob");
        System.out.println(driverService.create(driverJhon));
        System.out.println(driverService.create(driverAlice));
        System.out.println(driverService.create(driverBob));

        System.out.println("Cars:");
        Car rav4 = new Car("Rav4", toyota);
        Car s500 = new Car("S500", mercedes);
        Car soul = new Car("Soul", kia);
        rav4.setDrivers(new ArrayList<>(List.of(driverJhon, driverAlice)));
        s500.setDrivers(new ArrayList<>(List.of(driverBob)));
        soul.setDrivers(new ArrayList<>(List.of(driverJhon)));
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(rav4));
        carService.create(s500);
        carService.create(soul);
        s500.setModel("s550");
        carService.addDriverToCar(driverBob, soul);
        System.out.println(carService.update(s500));
        System.out.println(carService.update(soul));

        System.out.println("\nAll Cars by driver: " + driverJhon);
        carService.getAllByDriver(driverJhon.getId())
                .stream().forEach(System.out::println);

        System.out.println("\nRemove driver from car: ");
        carService.removeDriverFromCar(driverJhon, soul);
        carService.getAllByDriver(driverJhon.getId())
                .stream().forEach(System.out::println);

        System.out.println("Get s500 info: " + carService.get(s500.getId()));

        System.out.println("Delete driver: " + carService.delete(driverJhon.getId()));

        carService.getAll().stream().forEach(System.out::println);
    }
}
