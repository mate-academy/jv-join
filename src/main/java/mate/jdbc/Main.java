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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        System.out.println("FILLING MANUFACTURERS DB:");
        List<Manufacturer> manufacturers = List.of(
                new Manufacturer(null, "VW", "Germany"),
                new Manufacturer(null, "Ford", "US"),
                new Manufacturer(null, "Hyundai", "South Korea"),
                new Manufacturer(null, "Toyota", "Japan"));
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        for (Manufacturer manufacturer : manufacturers) {
            manufacturerService.create(manufacturer);
        }
        System.out.println("Done");
        System.out.println("FILLING DRIVERS:");
        List<Driver> drivers = List.of(
                new Driver(null, "Bill", "B-001"),
                new Driver(null, "Jim", "J-002"),
                new Driver(null, "Frank", "F-003"),
                new Driver(null, "Lucy", "L-004"),
                new Driver(null, "Kate", "K-005"));
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        for (Driver driver : drivers) {
            driverService.create(driver);
        }
        driverService.delete(5L);
        System.out.println("Done");
        System.out.println("CREATING CARS:");
        List<Car> cars = List.of(
                new Car(null, "Golf", manufacturerService.get(1L)),
                new Car(null, "Passat", manufacturerService.get(1L)),
                new Car(null, "Mustang", manufacturerService.get(2L)),
                new Car(null, "Corolla", manufacturerService.get(4L)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        for (Car car : cars) {
            System.out.println(carService.create(car));
        }
        Car car = new Car(null, "Camry", manufacturerService.get(4L));
        car.setDrivers(List.of(
                new Driver(1L, "Bill", "B-001"),
                new Driver(2L, "Jim", "J-002")));
        System.out.println(carService.create(car));
        System.out.println("DELETING CAR:");
        System.out.println(carService.delete(3L));
        System.out.println("GET CAR:");
        System.out.println(carService.get(1L));
        System.out.println(carService.get(5L));
        // The commented statement below throw exception for the deleted car:
        // System.out.println(carService.get(3L));
        System.out.println("GET ALL CARS:");
        carService.getAll().forEach(System.out::println);
        System.out.println("UPDATING CAR:");
        car = carService.get(1L);
        car.setDrivers(List.of(
                driverService.get(3L), driverService.get(4L)));
        System.out.println(carService.update(car));
        car.setDrivers(List.of(driverService.get(3L)));
        System.out.println(carService.update(car));
        System.out.println("ADD DRIVER TO CAR:");
        // The commented statement below throw exception for the deleted driver:
        // Driver driver = driverService.get(5L);
        Driver driver = driverService.get(3L);
        car = carService.get(5L);
        carService.addDriverToCar(driver, car);
        driver = driverService.get(4L);
        carService.addDriverToCar(driver, car);
        System.out.println("Method worked");
        System.out.println("REMOVE DRIVER TO CAR:");
        carService.removeDriverFromCar(driver, car);
        System.out.println("Method worked");
        System.out.println("GET CARS BY DRIVER:");
        carService.getAllByDriver(3L).forEach(System.out::println);
        System.out.println("ALL TESTS FINISHED");
    }
}
