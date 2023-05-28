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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer roga = new Manufacturer("NDI Roga i Kopyta", "Lohvycya");
        manufacturerService.create(roga);

        Manufacturer poizd = new Manufacturer("NDI Zabacanyi poizd", "Polonyna");
        manufacturerService.create(poizd);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver adam = new Driver("Adam", "10151810");
        driverService.create(adam);

        Driver bruno = new Driver("Bruno", "01041206");
        driverService.create(bruno);

        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car druchok = new Car("Druchok");
        druchok.setManufacturer(manufacturerService.get(1L));
        druchok.setDrivers(List.of(driverService.get(1L)));
        carService.create(druchok);

        Car pepelac = new Car("Pepelac");
        pepelac.setManufacturer(manufacturerService.get(2L));
        pepelac.setDrivers(List.of(driverService.get(1L), driverService.get(2L)));
        carService.create(pepelac);

        carService.removeDriverFromCar(driverService.get(2L), carService.get(2L));

        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println("========================");
        carService.addDriverToCar(driverService.get(2L), carService.get(1L));
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
