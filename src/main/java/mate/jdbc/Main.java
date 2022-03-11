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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    //
    private static final Manufacturer FIRST_MANUFACTURER =
            manufacturerService.create(new Manufacturer("Audi","Germany"));
    private static final Manufacturer SECOND_MANUFACTURER =
            manufacturerService.create(new Manufacturer("Ford", "America"));
    private static final Manufacturer THIRD_MANUFACTURER =
            manufacturerService.create(new Manufacturer("Opel", "Germany"));
    //
    private static final Driver FIRST_DRIVER =
            driverService.create(new Driver("Frank", "12345"));
    private static final Driver SECOND_DRIVER =
            driverService.create(new Driver("Alex", "67890"));
    private static final Driver THIRD_DRIVER =
            driverService.create(new Driver("Bob", "54321"));
    //
    private static final List<Driver> FIRST_CAR_DRIVERS =
            new ArrayList<>(List.of(FIRST_DRIVER));
    private static final List<Driver> SECOND_CAR_DRIVERS =
            new ArrayList<>(List.of(SECOND_DRIVER, THIRD_DRIVER));
    private static final List<Driver> THIRD_CAR_DRIVERS =
            new ArrayList<>(List.of(FIRST_DRIVER));
    private static final List<Driver> FOURTH_CAR_DRIVERS =
            new ArrayList<>(List.of(FIRST_DRIVER));

    public static void main(String[] args) {
        //
        // test DriverServiceImpl.create()
        //
        Car car1 = carService.create(new Car("RS6",FIRST_MANUFACTURER, FIRST_CAR_DRIVERS));
        System.out.println("Create new car: " + car1);
        Car car2 = carService.create(new Car("Focus",SECOND_MANUFACTURER, SECOND_CAR_DRIVERS));
        System.out.println("Create new car: " + car2);
        Car car3 = carService.create(new Car("Omega",THIRD_MANUFACTURER, THIRD_CAR_DRIVERS));
        System.out.println("Create new car: " + car3);
        Car car4 = carService.create(new Car("Vectra",THIRD_MANUFACTURER, FOURTH_CAR_DRIVERS));
        System.out.println("Create new car: " + car4);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.getAll()
        //
        System.out.println("Result of calling method CarServiceImpl.getAll(): ");
        carService.getAll().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.get()
        //
        Car carWithId1 = carService.get(1L);
        System.out.println("Result of calling method CarServiceImpl.get(1): " + carWithId1);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.update()
        //
        car2.setModel("Fiesta");
        carService.update(car2);
        System.out.println("Result of calling method CarServiceImpl.getAll() "
                + "after updating car with id = 2: ");
        carService.getAll().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.delete()
        //
        boolean isDeleted = carService.delete(4L);
        System.out.println("Car with id = 4 is deleted: " + isDeleted);
        System.out.println("Result of calling method CarServiceImpl.getAll() "
                + "after deleting car with id = 4: ");
        carService.getAll().forEach(System.out::println);
        //
        // test DriverServiceImpl.getAllByDriver()
        //
        System.out.println("Result of calling method CarServiceImpl.getAllByDriver() "
                + "with driver id = 1: ");
        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.addDriverToCar()
        //
        System.out.println("Third car drivers before adding new driver:");
        carService.get(3L).getDrivers().forEach(System.out::println);
        carService.addDriverToCar(THIRD_DRIVER, car3);
        System.out.println("Third car drivers after adding new driver:");
        carService.get(3L).getDrivers().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.removeDriverFromCar()
        //
        System.out.println("Third car drivers before removing driver with id = 3:");
        carService.get(3L).getDrivers().forEach(System.out::println);
        carService.removeDriverFromCar(THIRD_DRIVER, car3);
        System.out.println("Third car drivers after removing driver with id = 3:");
        carService.get(3L).getDrivers().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
    }
}
