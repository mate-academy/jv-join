package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarsService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        Manufacturer fiatManufacturer = new Manufacturer("Fiat", "Italy");
        Manufacturer renaultManufacturer = new Manufacturer("Renault", "France");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(fiatManufacturer);
        manufacturerService.create(renaultManufacturer);
        System.out.println("get manufacturer with id = " + audiManufacturer.getId() + " --- "
                + manufacturerService.get(audiManufacturer.getId()));
        System.out.println("get all manufacturers --- " + manufacturerService.getAll());
        Manufacturer toyotaManufacturer = new Manufacturer(
                fiatManufacturer.getId(), "Toyota", "Japan");
        manufacturerService.update(toyotaManufacturer);
        System.out.println("get manufacturer after update --- "
                        + manufacturerService.get(toyotaManufacturer.getId()));
        manufacturerService.delete(renaultManufacturer.getId());
        System.out.println("manufacturers after deletion: " + renaultManufacturer + " --- "
                + manufacturerService.getAll());
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
        System.out.println("All drivers: --- " + driverService.getAll());
        System.out.println("get driver by id = " + irynaDriver.getId() + " --- "
                + driverService.get(irynaDriver.getId()));
        Driver oleksandrDriver = new Driver(mykolaDriver.getId(), "Oleksandr", "TTR 448125");
        driverService.update(oleksandrDriver);
        System.out.println("driver after update --- " + driverService.get(oleksandrDriver.getId()));
        driverService.delete(ihorDriver.getId());
        System.out.println("All drivers --- " + driverService.getAll());
        System.out.println(System.lineSeparator());

        Car carAudiA8 = new Car("A8", audiManufacturer,
                List.of(petroDriver));
        CarsService carsService = (CarsService) injector.getInstance(CarsService.class);
        carsService.create(carAudiA8);
        System.out.println("get car with id = " + carAudiA8.getId() + " --- "
                + carsService.get(carAudiA8.getId()));
        Car carToyotaCamry = new Car("Camry", toyotaManufacturer,
                List.of(oleksandrDriver, irynaDriver));
        carsService.create(carToyotaCamry);
        System.out.println("All cars --- " + carsService.getAll());
        Car carAudiA6 = new Car(carAudiA8.getId(), "A6", audiManufacturer,
                List.of(oleksandrDriver, irynaDriver));
        carsService.update(carAudiA6);
        System.out.println(carAudiA8 + " after update --- " + carsService.get(1L));
        carsService.delete(carToyotaCamry.getId());
        System.out.println("AFTER DELETION Toyota: " + carsService.getAll());
        Driver bohdanDriver = new Driver("Bohdan", "WDE 741558");
        driverService.create(bohdanDriver);
        carsService.addDriverToCar(bohdanDriver, carAudiA6);
        System.out.println("Check drivers after add " + bohdanDriver + " --- "
                + carsService.get(carAudiA6.getId()));
        carsService.removeDriverFromCar(bohdanDriver, carAudiA6);
        System.out.println("AFTER BOHDAN DRIVER DELETION :" + carsService.get(1L));
        System.out.println(carsService.getAllByDriver(irynaDriver.getId()));
    }
}
