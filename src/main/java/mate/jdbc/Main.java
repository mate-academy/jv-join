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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver michael = new Driver(null,"Michael", "777");
        Driver frank = new Driver(null,"Frank", "941");
        Driver rob = new Driver(null,"Rob", "142");
        Driver bob = new Driver(null,"Bob", "548");
        driverService.create(michael);
        driverService.create(frank);
        driverService.create(rob);
        driverService.create(bob);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer koenigseggManufacturer = new Manufacturer(null,"Koenigsegg", "Sweden");
        Manufacturer bugattiManufacturer = new Manufacturer(null,"Bugatti", "France");
        manufacturerService.create(koenigseggManufacturer);
        manufacturerService.create(bugattiManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car koenigsegg = new Car("Agera RS ", koenigseggManufacturer, List.of(michael, frank));
        Car bugatti = new Car("Veyron", bugattiManufacturer, List.of(rob, bob));
        carService.create(koenigsegg);
        carService.create(bugatti);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(michael, koenigsegg);
        carService.removeDriverFromCar(bob, bugatti);
        carService.addDriverToCar(bob, koenigsegg);
        carService.addDriverToCar(michael, bugatti);
        System.out.println(carService.get(koenigsegg.getId()));
        System.out.println(carService.get(bugatti.getId()));
        carService.getAllByDriver(michael.getId()).forEach(System.out::println);
    }
}
