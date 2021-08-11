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
        System.out.println("Car's list now is: ");
        carService.getAll().forEach(System.out::println);

        carService.delete(3L);
        System.out.println("\nAfter deleting Lamborghini Urus the car list is:");
        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(drivers.get(0), cars.get(0));
        carService.addDriverToCar(drivers.get(1), cars.get(1));
        carService.addDriverToCar(drivers.get(1), cars.get(3));
        carService.addDriverToCar(drivers.get(2), cars.get(3));
        carService.addDriverToCar(drivers.get(2), cars.get(4));
        carService.addDriverToCar(drivers.get(3), cars.get(4));
        carService.addDriverToCar(drivers.get(3), cars.get(5));
        System.out.println("\nAfter adding drivers to the cars, car's list is:");
        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(drivers.get(0), cars.get(0));
        carService.removeDriverFromCar(drivers.get(1), cars.get(1));
        carService.removeDriverFromCar(drivers.get(2), cars.get(3));
        System.out.println("\nAfter removing drivers from the cars, car's list is:");
        carService.getAll().forEach(System.out::println);

        System.out.println("\nAll cars of driver with id = 4:");
        carService.getAllByDriver(4L).forEach(System.out::println);

        System.out.println("\nThe car with id = 5 is " + carService.get(5L));

        cars.get(4).setModel("Panamera");
        carService.update(cars.get(4));
        System.out.println("\nAfter updating Porsche Panamerraa the car is " + carService.get(5L));
    }

    private static void createManufacturers() {
        manufacturers = new ArrayList<>();
        manufacturers.add(new Manufacturer("Ferrari", "Italy"));
        manufacturers.add(new Manufacturer("Lamborghini", "Italy"));
        manufacturers.add(new Manufacturer("Porsche", "Germany"));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturers.forEach(manufacturerService::create);
    }

    private static void createCars() {
        cars = new ArrayList<>();
        cars.add(new Car("SF 90", manufacturers.get(0)));
        cars.add(new Car("Roma", manufacturers.get(0)));
        cars.add(new Car("Urus", manufacturers.get(1)));
        cars.add(new Car("Huracan", manufacturers.get(1)));
        cars.add(new Car("Panamerraa", manufacturers.get(2)));
        cars.add(new Car("Taycan", manufacturers.get(2)));
    }

    private static void createDrivers() {
        drivers = new ArrayList<>();
        drivers.add(new Driver("John Linkman", "UT128-645"));
        drivers.add(new Driver("Samantha Johns", "IM651-257"));
        drivers.add(new Driver("Lukas Greenberg", "KT822-040"));
        drivers.add(new Driver("Alfred Lemme", "MY-214-003"));

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        drivers.forEach(driverService::create);
    }
}
