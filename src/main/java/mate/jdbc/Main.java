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
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        manufacturerService.create(new Manufacturer("Audi", "Germany"));
        manufacturerService.create(new Manufacturer("BMW", "Germany"));
        manufacturerService.create(new Manufacturer("Mercedes", "Germany"));
        manufacturerService.create(new Manufacturer("Tesla", "USA"));
        System.out.println("\n manufacturers created: ");
        manufacturerService.getAll().forEach(System.out::println);
        driverService.create(new Driver("John", "L12"));
        driverService.create(new Driver("Mike", "L13"));
        driverService.create(new Driver("Bob", "L14"));
        driverService.create(new Driver("Tom", "L15"));
        driverService.create(new Driver("Jack", "L16"));
        driverService.create(new Driver("Nik", "L17"));
        System.out.println("\n drivers created: ");
        driverService.getAll().forEach(System.out::println);

        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(driverService.get(1L));
        drivers1.add(driverService.get(2L));
        Car car1 = new Car();
        car1.setModel("X8");
        car1.setManufacturer(manufacturerService.get(2L));
        car1.setDrivers(drivers1);
        carService.create(car1);
        System.out.println("\n car1 created: ");
        System.out.println(carService.get(car1.getId()));

        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driverService.get(3L));
        drivers2.add(driverService.get(4L));
        Car car2 = new Car();
        car2.setModel("TT");
        car2.setManufacturer(manufacturerService.get(1L));
        car2.setDrivers(drivers2);
        carService.create(car2);
        System.out.println("\n car2 created: ");
        System.out.println(carService.get(car2.getId()));

        List<Driver> drivers3 = new ArrayList<>();
        drivers3.add(driverService.get(2L));
        drivers3.add(driverService.get(3L));
        drivers3.add(driverService.get(4L));
        Car car3 = new Car();
        car3.setModel("Model Y");
        car3.setManufacturer(manufacturerService.get(4L));
        car3.setDrivers(drivers3);
        carService.create(car3);
        System.out.println("\n car3 created: ");
        System.out.println(carService.get(car3.getId()));

        List<Driver> drivers3Upd = new ArrayList<>();
        drivers3Upd.add(driverService.get(3L));
        drivers3Upd.add(driverService.get(4L));
        drivers3Upd.add(driverService.get(5L));
        car3.setModel("Model X");
        car3.setDrivers(drivers3Upd);
        carService.update(car3);
        System.out.println("\n car3 updated: ");
        System.out.println(carService.get(car3.getId()));

        System.out.println();
        carService.getAll().forEach(System.out::println);

        carService.delete(1L);

        System.out.println("\n car1 deleted: ");
        carService.getAll().forEach(System.out::println);

        System.out.println("\n getAllByDriver: ");
        carService.getAllByDriver(3L).forEach(System.out::println);

        System.out.println("\n addDriverToCar: ");
        carService.addDriverToCar(driverService.get(4L), carService.get(2L));
        carService.getAllByDriver(4L).forEach(System.out::println);

        System.out.println("\n removeDriverFromCar: ");
        carService.removeDriverFromCar(driverService.get(4L), carService.get(2L));
        carService.getAllByDriver(4L).forEach(System.out::println);

    }
}
