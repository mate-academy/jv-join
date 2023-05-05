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
    private static final Injector injector = Injector.getInstance(Main.class.getPackageName());
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        List<Driver> drivers = List.of(
                new Driver("Franklin Delano Roosevelt", "123456789"),
                new Driver("Donald John Trump", "987654321"),
                new Driver("Ronald Wilson Reagan", "777777777")
        );

        for (Driver driver : drivers) {
            driverService.create(driver);
        }

        List<Manufacturer> manufacturers = List.of(
                new Manufacturer("Ford", "USA"),
                new Manufacturer("Bentley", "Great Britain"),
                new Manufacturer("Mercedes-Benz", "Germany")
        );

        for (Manufacturer manufacturer : manufacturers) {
            manufacturerService.create(manufacturer);
        }

        List<Car> cars = List.of(
                new Car(null, "Spur", manufacturerService.get(2L),
                        List.of(driverService.get(1L),driverService.get(2L))),
                new Car(null, "E200", manufacturerService.get(3L),
                        List.of(driverService.get(2L),driverService.get(3L))),
                new Car(null, "Mondeo", manufacturerService.get(1L),
                        List.of())
        );

        System.out.println("\nTest create car and get all cars:");
        for (Car car : cars) {
            carService.create(car);
        }
        carService.getAll().forEach(System.out::println);

        Long id = 3L;
        System.out.println("\nTest delete car by id = " + id + ":");
        System.out.println("before get(" + id + ") = " + carService.get(id));
        System.out.println("result delete operation = " + carService.delete(id));
        System.out.println("after get(" + id + ") = " + carService.get(id));

        id = 1L;
        System.out.println("\nTest get car by id = " + id + ":");
        System.out.println(carService.get(id));

        System.out.println("\nTest get all cars by driver id = " + id + ":");
        carService.getAllByDriver(id).forEach(System.out::println);

        System.out.println("\nTest add driver to car:");
        Car carFromDb = carService.get(id);
        System.out.println("before get(" + id + ") = " + carFromDb);
        Driver driverFromDb = driverService.get(3L);
        System.out.println("add driver = " + driverFromDb);
        carService.addDriverToCar(driverFromDb, carFromDb);
        carFromDb = carService.get(id);
        System.out.println("after get(" + id + ") = " + carFromDb);

        System.out.println("\nTest remove driver from car:");
        System.out.println("before get(" + id + ") = " + carFromDb);
        driverFromDb = driverService.get(3L);
        System.out.println("remove driver = " + driverFromDb);
        carService.removeDriverFromCar(driverFromDb, carFromDb);
        carFromDb = carService.get(id);
        System.out.println("after get(" + id + ") = " + carFromDb);
    }
}

