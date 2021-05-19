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
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        System.out.println("_______________________________"
                + '\n' + "Testing is table empty");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
        System.out.println();

        Manufacturer scoda = new Manufacturer("Scoda","Czech Republic");
        manufacturerService.create(scoda);
        Manufacturer volkswagen = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(volkswagen);
        Manufacturer bugatti = new Manufacturer("Bugatti", "France");
        manufacturerService.create(bugatti);
        Manufacturer porsche = new Manufacturer("Porsche", "Germany");
        manufacturerService.create(porsche);

        List<Driver> drivers = new ArrayList<>();
        Driver tom = new Driver("Tom", "ABC123");
        driverService.create(tom);
        drivers.add(tom);
        Driver john = new Driver("John", "DEFG45");
        driverService.create(john);
        drivers.add(john);
        Driver kate = new Driver("Kate", "HJK678");
        driverService.create(kate);
        drivers.add(kate);
        Driver evelyne = new Driver("Evelyne", "LMNO90");
        driverService.create(evelyne);

        Car scodaFabia = new Car("Fabia", scoda, drivers);
        carService.create(scodaFabia);
        Car volkswagenGolf = new Car("Golf", volkswagen, drivers);
        carService.create(volkswagenGolf);

        System.out.println("_______________________________");
        System.out.println("Testing create and read (get by id)");
        System.out.println(manufacturerService.get(scoda.getId()));
        System.out.println(manufacturerService.get(volkswagen.getId()));
        System.out.println(manufacturerService.get(bugatti.getId()));
        System.out.println(manufacturerService.get(porsche.getId()));
        System.out.println(driverService.get(tom.getId()));
        System.out.println(driverService.get(john.getId()));
        System.out.println(driverService.get(kate.getId()));
        System.out.println(driverService.get(evelyne.getId()));
        System.out.println(carService.get(scodaFabia.getId()));
        System.out.println(carService.get(volkswagenGolf.getId()));

        bugatti.setName("Bugatti Automobiles");
        manufacturerService.update(bugatti);
        manufacturerService.delete(porsche.getId());
        kate.setLicenseNumber("NEW777");
        driverService.update(kate);
        driverService.delete(evelyne.getId());
        List<Driver> newDrivers = new ArrayList<>();
        newDrivers.add(tom);
        newDrivers.add(kate);
        scodaFabia.setModel("Fabia GTI");
        scodaFabia.setDrivers(newDrivers);
        carService.update(scodaFabia);
        carService.delete(volkswagenGolf.getId());

        System.out.println("_______________________________");
        System.out.println("Testing read (get all), update and delete");
        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
    }
}
