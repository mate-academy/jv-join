package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        // test your code here
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);

        // getAll data from DataBase
        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);

        // initialize field values using setters or constructor
        System.out.println("Initialize field values using setters or constructor");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("TestManufacturer");
        manufacturer.setCountry("TestCountry");
        manufacturer = manufacturerService.create(manufacturer);

        Driver driver = new Driver();
        driver.setName("TestDriver");
        driver.setLicenseNumber("14253");
        driver = driverService.create(driver);

        // test other methods from ManufacturerDao
        // get by id
        System.out.println("Get by id method testing");
        manufacturer = manufacturerService.get(manufacturer.getId());
        System.out.println(manufacturer);
        Manufacturer nonExistIdManufacturer = new Manufacturer();
        nonExistIdManufacturer.setId(5L);
        nonExistIdManufacturer = manufacturerService.get(nonExistIdManufacturer.getId());
        System.out.println(nonExistIdManufacturer);

        driver = driverService.get(driver.getId());
        System.out.println(driver);
        Driver nonExistIdDriver = new Driver();
        nonExistIdDriver.setId(5L);
        nonExistIdDriver = driverService.get(nonExistIdDriver.getId());
        System.out.println(nonExistIdDriver);

        // update
        System.out.println("Update method testing");
        manufacturer.setName("TestManufacturer1");
        manufacturer.setCountry("TestCountry1");
        manufacturer.setId(6L);
        System.out.println(manufacturerService.update(manufacturer));
        manufacturer = new Manufacturer("TestManufacturer2", "TestCountry2");
        System.out.println(manufacturerService.update(manufacturer));

        driver.setName("Driver3");
        driver.setLicenseNumber("3333333");
        driver.setId(6L);
        System.out.println(driverService.update(driver));
        driver = new Driver("Driver4", "4444444");
        System.out.println(driverService.update(driver));

        //delete
        System.out.println("Delete method testing");
        System.out.println(manufacturerService.delete(4L));

        System.out.println(driverService.delete(4L));

        // getAll data from DataBase
        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);


    }
}
