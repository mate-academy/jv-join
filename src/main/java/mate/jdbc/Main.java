package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        System.out.println("Get all drivers:");
        driverService.getAll().forEach(System.out::println);
        System.out.println();
        System.out.println("Get all manufacturer");
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println();

        Driver driver1 = new Driver("Max", "SS-2442");
        Driver driver2 = new Driver("Gosha", "SA-3334");
        //driverService.create(driver1);
        //driverService.create(driver2);
        Manufacturer manufacturer1 = new Manufacturer("Mercedes-Benz", "Germany");
        Manufacturer manufacturer2 = new Manufacturer("Chevrolet", "USA");
        //manufacturerService.create(manufacturer1);
        //manufacturerService.create(manufacturer2);
        System.out.println("Get all drivers:");
        driverService.getAll().forEach(System.out::println);
        System.out.println();
        System.out.println("Get all manufacturer");
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println();

        System.out.println("Get drivers by id = 2");
        System.out.println(driverService.get(2L));
        System.out.println("Get manufacturer by id = 2");
        System.out.println(manufacturerService.get(2L));
        System.out.println();

        driver1.setName("KLAVA");
        manufacturer1.setCountry("russia");
        System.out.println("Try update driver");
        System.out.println(driver1);
        driver1 = driverService.update(driver1);
        System.out.println("result after update");
        System.out.println(driverService.get(driver1.getId()));
        System.out.println("Try update manufacturer");
        System.out.println(manufacturer1);
        manufacturer1 = manufacturerService.update(manufacturer1);
        System.out.println("result after update");
        System.out.println(manufacturerService.get(manufacturer1.getId()));
        System.out.println();

        driverService.delete(1L);
        manufacturerService.delete(1L);
        System.out.println("Delete and get drivers by id = 1");
        System.out.println(driverService.get(1L));
        System.out.println("Delete and get manufacturer by id = 1");
        System.out.println(manufacturerService.get(1L));
        System.out.println();
    }
}
