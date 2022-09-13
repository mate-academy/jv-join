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

        Manufacturer mercedesBenz = new Manufacturer("Mercedes-Benz", "Germany");
        manufacturerService.create(mercedesBenz);

        Manufacturer mercedesBenzC220 = new Manufacturer(
                "Mercedes-Benz C 220 d 4 MATIC", "Germany");
        manufacturerService.create(mercedesBenzC220);

        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);

        List<Driver> mercedesDrivers = new ArrayList<>();

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver bob = new Driver("Bob", "495904");
        driverService.create(bob);
        mercedesDrivers.add(bob);

        Driver alice = new Driver("Alice", "854755");
        driverService.create(alice);
        mercedesDrivers.add(alice);

        List<Driver> bmwDrivers = new ArrayList<>();
        Driver jack = new Driver("Jack", "857456");
        driverService.create(jack);
        bmwDrivers.add(jack);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car bmwCar = new Car("BMW", bmw, bmwDrivers);
        carService.create(bmwCar);

        Car mercedesCar = new Car("Mercedes-Benz", mercedesBenz, mercedesDrivers);
        carService.create(mercedesCar);

        System.out.println("ALL DATA BASE:");
        carService.getAll().forEach(System.out::println);
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println();

        carService.addDriverToCar(jack, bmwCar);
        System.out.println(carService.get(bmwCar.getId()));
        System.out.println(jack + " was add to car");
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println();
    }
}
