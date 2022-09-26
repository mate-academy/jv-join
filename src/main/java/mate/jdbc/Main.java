package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        /*Manufacturer newManufacturer =
                manufacturerService.create(Manufacturer.of("Renault", "France"));
        Driver newDriver1 = driverService.create(Driver.of("Jason Statham", "OPU987098"));
        Driver newDriver2 = driverService.create(Driver.of("Sargis Hakobyan", "JHG765543"));
        Driver existingDriver = driverService.get(8L);
        List<Driver> drivers = List.of(newDriver1, newDriver2, existingDriver);
        System.out.println(carService.create(Car.of("Logan", newManufacturer, drivers)));*/
        //System.out.println(carService.get(3L));
        //System.out.println(carService.delete(14L));
        //carService.getAll().forEach(System.out::println);
        //carService.getAllByDriver(3L).forEach(System.out::println);


    }
}
