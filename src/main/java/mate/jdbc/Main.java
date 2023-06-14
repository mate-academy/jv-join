package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarsService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.temp.DatabaseInitializer;

public class Main {
    private static final String INIT_DB_FILE_PATH = "src/main/resources/init_db.sql";
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DatabaseInitializer databaseInitializer =
                (DatabaseInitializer) injector.getInstance(DatabaseInitializer.class);
        String[] initQueries = databaseInitializer.readFromFile(INIT_DB_FILE_PATH);
        databaseInitializer.initializeDb(initQueries);

        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        Manufacturer fiatManufacturer = new Manufacturer("Fiat", "Italy");
        Manufacturer renaultManufacturer = new Manufacturer("Renault", "France");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(fiatManufacturer);
        manufacturerService.create(renaultManufacturer);
        System.out.println(manufacturerService.get(audiManufacturer.getId()));
        System.out.println(manufacturerService.getAll());
        Manufacturer toyotaManufacturer = new Manufacturer(2L, "Toyota", "Japan");
        manufacturerService.update(toyotaManufacturer);
        System.out.println(manufacturerService.get(toyotaManufacturer.getId()));
        manufacturerService.delete(renaultManufacturer.getId());
        System.out.println(manufacturerService.getAll());
        System.out.println(System.lineSeparator());

        Driver petroDriver = new Driver("Petro", "AAC 445972");
        Driver mykolaDriver = new Driver("Mykola", "AAC 453789");
        Driver irynaDriver = new Driver("Iryna", "AAX 115675");
        Driver ihorDriver = new Driver("Ihor", "AAC 567763");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(petroDriver);
        driverService.create(mykolaDriver);
        driverService.create(irynaDriver);
        driverService.create(ihorDriver);
        System.out.println(driverService.getAll());
        System.out.println(driverService.get(irynaDriver.getId()));
        Driver oleksandrDriver = new Driver(2L, "Oleksandr", "TTR 448125");
        driverService.update(oleksandrDriver);
        System.out.println(driverService.get(oleksandrDriver.getId()));
        driverService.delete(4L);
        System.out.println(driverService.getAll());
        System.out.println(System.lineSeparator());

        Car carAudiA8 = new Car("A8", audiManufacturer,
                List.of(petroDriver));
        CarsService carsService = (CarsService) injector.getInstance(CarsService.class);
        carsService.create(carAudiA8);
        System.out.println(carsService.get(1L));
        Car carToyotaCamry = new Car("Camry", toyotaManufacturer,
                List.of(oleksandrDriver, irynaDriver));
        carsService.create(carToyotaCamry);
        System.out.println(carsService.getAll());
        Car carAudiA6 = new Car(1L, "A6", audiManufacturer,
                List.of(oleksandrDriver, irynaDriver));
        carsService.update(carAudiA6);
        System.out.println(carsService.get(1L));
        carsService.delete(2L);
        System.out.println("AFTER DELETION Toyota: " + carsService.getAll());
        Driver bohdanDriver = new Driver("Bohdan", "WDE 741558");
        driverService.create(bohdanDriver);
        carsService.addDriverToCar(bohdanDriver, carAudiA6);
        System.out.println(carsService.get(1L));
        carsService.removeDriverFromCar(bohdanDriver, carAudiA6);
        System.out.println("AFTER BOHDAN DRIVER DELETION :" + carsService.get(1L));
        System.out.println(carsService.getAllByDriver(3L));
    }
}
