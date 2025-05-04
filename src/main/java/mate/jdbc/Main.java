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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Manufacturer mercedes = manufacturerService.create(
                new Manufacturer(null, "Mercedes", "Germany"));

        Driver ivan = driverService.create(new Driver(null, "Ivan", "AAA 111 222"));

        Car car = carService.create(new Car(null, "Mercedes GLE 400", mercedes,
                List.of(ivan)));

        carService.getAll().forEach(System.out::println);

        System.out.println("\n\n--- was added drivers to Ferrari ---");
        Driver petr = driverService.create(new Driver(null, "Petr", "BBB 222 333"));
        carService.addDriverToCar(petr, car);
        carService.getAll().forEach(System.out::println);

        System.out.println("\n\n--- was removed driver Ivan from car ---");
        carService.removeDriverFromCar(ivan, car);
        carService.getAll().forEach(System.out::println);

        System.out.println("\n\n--- car was updated with model name ---");
        car.setModel("Mercedes Coupe");
        System.out.println(carService.update(car));

        System.out.println("\n\n--- get cars by driver's ID ---");
        System.out.println(carService.getAllByDriver(ivan.getId()));

        System.out.println("\n\n--- car was deleted ---");
        System.out.println(carService.delete(car.getId()));
    }
}
