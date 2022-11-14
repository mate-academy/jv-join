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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer ford = manufacturerService
                .create(new Manufacturer(null, "Ford", "USA"));
        Driver kenMiles = driverService.create(new Driver(null,
                        "Ken Miles", "335 667 223"));
        Driver carrollShelby = driverService.create(new Driver(null,
                        "Carroll Shelby", "332 123 613"));
        List<Driver> fordShelbyDrivers = List.of(kenMiles, carrollShelby);
        Car fordShelby = carService.create(new Car("Shelby", ford, fordShelbyDrivers));
        System.out.println("get ford shelby: ");
        System.out.println(carService.get(fordShelby.getId()));
        System.out.println("carService\ngetAll :");
        carService.getAll().forEach(System.out::println);
        System.out.println("getAllByDriver Ken: ");
        carService.getAllByDriver(kenMiles.getId()).forEach(System.out::println);
        System.out.println("getAllByDriver Carrol: ");
        carService.getAllByDriver(carrollShelby.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(carrollShelby, fordShelby);
        System.out.println("Car drivers after update: ");
        System.out.println(carService.get(fordShelby.getId()).getDrivers());
        System.out.println("getAllByDriver Carrol after deletion: ");
        carService.getAllByDriver(carrollShelby.getId()).forEach(System.out::println);
        carService.addDriverToCar(carrollShelby, fordShelby);
        System.out.println("getAllByDriver Carrol after inserting: ");
        carService.getAllByDriver(carrollShelby.getId()).forEach(System.out::println);
        System.out.println("Car drivers after update: ");
        System.out.println(carService.get(fordShelby.getId()).getDrivers());
    }
}
