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
    private static CarService carService = (CarService) injector.getInstance(CarService.class);
    private static DriverService driverService = (DriverService) injector
            .getInstance(DriverService.class);
    private static ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static List<Manufacturer> manufacturers;
    private static List<Car> cars;
    private static List<Driver> drivers;

    public static void main(String[] args) {
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        audi = manufacturerService.create(audi);
        System.out.println("Manufacturer audi: " + audi);

        drivers = new ArrayList<>();
        Driver misha = driverService.create(new Driver("Zubenko Michail Petrovych", "01234"));
        drivers.add(misha);
        Driver joe = driverService.create(new Driver("Joe", "56789"));
        drivers.add(joe);
        Driver bob = driverService.create(new Driver("Bob", "10111"));
        drivers.add(bob);
        System.out.println("List of drivers: ");
        drivers.forEach(System.out::println);

        Car q7 = carService.create(new Car("q7", audi, drivers));
        System.out.println(carService.get(q7.getId()));
        System.out.println("Car audi q7: " + q7);

        carService.removeDriverFromCar(misha, q7);
        carService.removeDriverFromCar(joe, q7);
        carService.addDriverToCar(bob, q7);
        System.out.println("Car audi q7 after changing driver: " + q7);

        List<Car> cars = carService.getAll();
        System.out.println("List of all cars ");
        cars.forEach(System.out::println);
        System.lineSeparator();
        System.out.println("All cars with driver Bob: ");
        System.out.println(carService.getAllByDriver(bob.getId()));
    }
}
