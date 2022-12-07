package mate.jdbc;

import java.util.List;
import java.util.NoSuchElementException;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector myInject = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        final DriverService driverService = (DriverService)
                myInject.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                myInject.getInstance(ManufacturerService.class);
        final CarService carService = (CarService)
                myInject.getInstance(CarService.class);

        manufacturerService.create(new Manufacturer("Daewoo", "Korea"));
        manufacturerService.create(new Manufacturer("Opel", "Germany"));
        manufacturerService.create(new Manufacturer("Renault", "France"));
        manufacturerService.create(new Manufacturer("Ford", "USA"));
        manufacturerService.create(new Manufacturer("Fiat", "Italy"));
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        manufacturerService.delete(1L);
        manufacturerService.update(new Manufacturer(2L, "Nissan", "Japan"));
        try {
            System.out.println(manufacturerService.get(1L));
        } catch (NoSuchElementException e) {
            System.out.println("Manufacturer deleted");
        }
        System.out.println(manufacturerService.get(2L));

        driverService.create(new Driver("Ivan", "2317245"));
        driverService.create(new Driver("Kolya", "6243121"));
        driverService.create(new Driver("Vasil", "4643121"));
        driverService.create(new Driver("Petro", "5645433"));
        driverService.create(new Driver("Bogdan", "6243121"));
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        driverService.delete(2L);
        driverService.update(new Driver(1L, "Victor", "5465454"));
        System.out.println(driverService.get(1L));
        try {
            System.out.println(driverService.get(2L));
        } catch (NoSuchElementException e) {
            System.out.println("Driver deleted");
        }
    }
}
