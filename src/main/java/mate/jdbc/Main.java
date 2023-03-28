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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer mercedes = new Manufacturer("Mercedes", "Germany");
        manufacturerService.create(mercedes);
        System.out.println(manufacturerService.get(mercedes.getId()));
        Driver antonio = new Driver("Antonio", "ET35647");
        Driver azario = new Driver("Azario", "ET35649");
        Driver pablo = new Driver("Pablo", "ET35648");
        driverService.create(antonio);
        driverService.create(azario);
        driverService.create(pablo);
        List<Driver> driversOne = List.of(antonio, azario, pablo);
        List<Driver> driversTwo = List.of(antonio, pablo);
        Car vito = new Car("Vito", mercedes,driversOne);
        Car sprinter = new Car("Sprinter", mercedes, driversTwo);
        carService.create(vito);
        carService.create(sprinter);
        Car incognito = carService.get(vito.getId());
        System.out.println(incognito.toString());
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(antonio.getId()).forEach(System.out::println);
        Manufacturer crab = new Manufacturer("Crab", "Ukraine");
        manufacturerService.create(crab);
        sprinter.setManufacturer(crab);
        carService.update(sprinter);
        carService.delete(vito.getId());
        System.out.println(carService.getAll());
    }
}
