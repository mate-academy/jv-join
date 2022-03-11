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

    public static void main(String[] args) {
        final Manufacturer firstManufacturer =
                manufacturerService.create(new Manufacturer("Audi","Germany"));
        final Manufacturer secondManufacturer =
                manufacturerService.create(new Manufacturer("Ford", "America"));
        final Manufacturer thirdManufacturer =
                manufacturerService.create(new Manufacturer("Opel", "Germany"));
        //
        final Driver firstDriver =
                driverService.create(new Driver("Frank", "12345"));
        final Driver secondDriver =
                driverService.create(new Driver("Alex", "67890"));
        final Driver thirdDriver =
                driverService.create(new Driver("Bob", "54321"));
        //
        final List<Driver> firstCarDrivers =
                new ArrayList<>(List.of(firstDriver));
        final List<Driver> secondCarDrivers =
                new ArrayList<>(List.of(secondDriver, thirdDriver));
        final List<Driver> thirdCarDrivers =
                new ArrayList<>(List.of(firstDriver));
        final List<Driver> fourthCarDrivers =
                new ArrayList<>(List.of(firstDriver));
        //
        // test DriverServiceImpl.create()
        //
        Car car1 = carService.create(new Car("RS6",firstManufacturer, firstCarDrivers));
        System.out.println("Create new car: " + car1);
        Car car2 = carService.create(new Car("Focus",secondManufacturer, secondCarDrivers));
        System.out.println("Create new car: " + car2);
        Car car3 = carService.create(new Car("Omega",thirdManufacturer, thirdCarDrivers));
        System.out.println("Create new car: " + car3);
        Car car4 = carService.create(new Car("Vectra",thirdManufacturer, fourthCarDrivers));
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
        Car carWithId1 = carService.get(car1.getId());
        System.out.println("Result of calling method CarServiceImpl.get(1): \n" + carWithId1);
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
        boolean isDeleted = carService.delete(car4.getId());
        System.out.println("Car with id = 4 is deleted: " + isDeleted);
        System.out.println("Result of calling method CarServiceImpl.getAll() "
                + "after deleting car with id = 4: ");
        carService.getAll().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.getAllByDriver()
        //
        System.out.println("Result of calling method CarServiceImpl.getAllByDriver() "
                + "with driver id = 1: ");
        carService.getAllByDriver(firstDriver.getId()).forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.addDriverToCar()
        //
        System.out.println("Third car drivers before adding new driver:");
        carService.get(car3.getId()).getDrivers().forEach(System.out::println);
        carService.addDriverToCar(thirdDriver, car3);
        System.out.println("Third car drivers after adding new driver:");
        carService.get(car3.getId()).getDrivers().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
        //
        // test DriverServiceImpl.removeDriverFromCar()
        //
        System.out.println("Third car drivers before removing driver with id = 3:");
        carService.get(car3.getId()).getDrivers().forEach(System.out::println);
        carService.removeDriverFromCar(thirdDriver, car3);
        System.out.println("Third car drivers after removing driver with id = 3:");
        carService.get(car3.getId()).getDrivers().forEach(System.out::println);
        System.out.println(".");
        System.out.println(".");
        System.out.println(".");
    }
}
