package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        System.out.println("App.start");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        Driver firstDriver = new Driver("Іванов Іван Іванович","3222233322");
        System.out.print(firstDriver + " -> ");
        Driver testDriver = driverService.create(firstDriver);
        System.out.println(testDriver);
        System.out.println(driverService.get(testDriver.getId()));
        Driver secondDriver = new Driver("Мазепа Т.П.","777");
        testDriver = driverService.create(secondDriver);
        System.out.print(secondDriver + " -> ");
        System.out.println(driverService.get(testDriver.getId()));
        System.out.print("\nNow update it to -> ");
        Driver updatebleDriver = testDriver;
        updatebleDriver.setName("П.Т. Мазепа");
        driverService.update(updatebleDriver);
        System.out.println(driverService.get(updatebleDriver.getId()));
        System.out.println("\nNow delete Driver id = "
                + testDriver.getId() + "  from DB ");
        if (driverService.delete(testDriver.getId())) {
            System.out.println("Driver id = " + testDriver.getId()
                    + " deleted successfully");
        }
        drivers = driverService.getAll();
        drivers.forEach(System.out::println);
        System.out.println("<---------------------------->");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        Manufacturer firstInputManufacturer = new Manufacturer("IBM2","USA");
        Manufacturer testManufacturer = manufacturerService.create(firstInputManufacturer);
        System.out.print("\n" + firstInputManufacturer + " -> ");
        System.out.println(manufacturerService.get(testManufacturer.getId()));
        Manufacturer secondInputManufacturer = new Manufacturer("BMV2","Germany");
        testManufacturer = manufacturerService.create(secondInputManufacturer);
        System.out.print(secondInputManufacturer + " -> ");
        System.out.println(manufacturerService.get(testManufacturer.getId()));
        System.out.print("\nNow update it to -> ");
        Manufacturer updatebleManufacturer = testManufacturer;
        updatebleManufacturer.setName("VW");
        manufacturerService.update(updatebleManufacturer);
        System.out.println(manufacturerService.get(updatebleManufacturer.getId()));
        System.out.println("\nNow delete Manufacturer id = "
                + testManufacturer.getId() + "  from DB ");
        if (manufacturerService.delete(testManufacturer.getId())) {
            System.out.println("Manufacturer id = " + testManufacturer.getId()
                    + " deleted successfully");
        }
        manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);
        System.out.println("App.finish");
    }
}
