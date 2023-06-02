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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("Created two cars: x5 and s500");
        List<Driver> s500Drivers = new ArrayList<>();
        Driver mark = driverService.create(new Driver(null, "Mark", "444"));
        s500Drivers.add(mark);
        Manufacturer mrcManufacturer =
                manufacturerService.create(new Manufacturer(null, "Mercedes Benz", "Germany"));
        Car s500 = carService.create(new Car(null, "s500", mrcManufacturer, s500Drivers));

        List<Driver> x5Drivers = new ArrayList<>();
        Driver sam = driverService.create(new Driver(null, "Sam", "555"));
        x5Drivers.add(sam);
        Driver bob = driverService.create(new Driver(null, "Bob", "666"));
        x5Drivers.add(bob);
        Manufacturer bmwManufacturer =
                manufacturerService.create(new Manufacturer(null, "BMW", "Germany"));
        Car x5 = carService.create(new Car(null, "X5", bmwManufacturer, x5Drivers));

        System.out.println("Get car by id = " + s500.getId());
        System.out.println(carService.get(x5.getId()));

        System.out.println("Get car by id = " + x5.getId());
        System.out.println(carService.get(x5.getId()));

        System.out.println("Update car s500");
        Driver alice = driverService.create(new Driver(null, "Alice", "333"));
        s500.setModel("s500 L");
        carService.update(s500);
        carService.addDriverToCar(alice, s500);
        carService.addDriverToCar(bob, s500);
        carService.removeDriverFromCar(mark, s500);

        System.out.println("Get all cars after update");
        carService.getAll().forEach(System.out::println);

        System.out.println("Get all cars with driver " + bob);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);

        System.out.println("Delete car with id = " + x5.getId());
        carService.delete(x5.getId());

        System.out.println("Get all cars after delete");
        carService.getAll().forEach(System.out::println);
    }
}
