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
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static List<Manufacturer> manufacturers;
    private static List<Car> cars;
    private static List<Driver> drivers;

    public static void main(String[] args) {
        createManufacturers();
        createCars();
        createDrivers();

        CarService carService = (CarService) injector.getInstance(CarService.class);

        cars.forEach(carService::create);
        System.out.println("List of cars before all operations: ");
        carService.getAll().forEach(System.out::println);

        carService.delete(3L);
        System.out.println("\nList of cars after Dodge Charger deleted");
        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(drivers.get(0), cars.get(0));
        carService.addDriverToCar(drivers.get(1), cars.get(1));
        carService.addDriverToCar(drivers.get(1), cars.get(3));
        carService.addDriverToCar(drivers.get(2), cars.get(3));
        carService.addDriverToCar(drivers.get(2), cars.get(4));
        carService.addDriverToCar(drivers.get(3), cars.get(4));
        carService.addDriverToCar(drivers.get(3), cars.get(5));
        System.out.println("\nList of cars after drivers added:");
        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(drivers.get(0), cars.get(0));
        carService.removeDriverFromCar(drivers.get(1), cars.get(1));
        carService.removeDriverFromCar(drivers.get(2), cars.get(3));
        System.out.println("\nList of cars after drivers removed:");
        carService.getAll().forEach(System.out::println);

        System.out.println("\nList of cars with driver which id = 4:");
        carService.getAllByDriver(4L).forEach(System.out::println);

        System.out.println("\nThe car with id = 5 is " + carService.get(5L));
    }

    private static void createManufacturers() {
        manufacturers = new ArrayList<>();
        manufacturers.add(new Manufacturer("BMW", "Germany"));
        manufacturers.add(new Manufacturer("Dodge", "USA"));
        manufacturers.add(new Manufacturer("Citroen", "France"));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturers.forEach(manufacturerService::create);
    }

    private static void createCars() {
        cars = new ArrayList<>();
        cars.add(new Car("X5", manufacturers.get(0)));
        cars.add(new Car("X6", manufacturers.get(0)));
        cars.add(new Car("Charger", manufacturers.get(1)));
        cars.add(new Car("Challenger", manufacturers.get(1)));
        cars.add(new Car("c3", manufacturers.get(2)));
        cars.add(new Car("c5", manufacturers.get(2)));
    }

    private static void createDrivers() {
        drivers = new ArrayList<>();
        drivers.add(new Driver("Joe", "123"));
        drivers.add(new Driver("Misha", "456"));
        drivers.add(new Driver("Hans", "789"));
        drivers.add(new Driver("Gerard", "101"));

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        drivers.forEach(driverService::create);
    }
}
