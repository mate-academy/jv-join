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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer saab = new Manufacturer("SAAB", "Sweden");
        Manufacturer jeep = new Manufacturer("Jeep", "USA");
        Manufacturer renault = new Manufacturer("Renault", "France");

        manufacturerService.create(saab);
        manufacturerService.create(jeep);
        manufacturerService.create(renault);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver morales = new Driver("Morales", "LIC-228");
        Driver toretto = new Driver("Toretto", "LIC-132");
        Driver okonnor = new Driver("O'Konnor", "LIC-911");

        driverService.create(morales);
        driverService.create(toretto);
        driverService.create(okonnor);
        Car saab900 = new Car();
        List<Driver> drivers = new ArrayList<>();
        drivers.add(morales);
        saab900.setModel("SAAB 900");
        saab900.setManufacturer(saab);
        saab900.setDrivers(drivers);
        Car renaultLogan = new Car();
        drivers.add(okonnor);
        renaultLogan.setModel("Logan");
        renaultLogan.setManufacturer(renault);
        renaultLogan.setDrivers(drivers);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        carService.create(saab900);
        carService.create(renaultLogan);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(toretto, saab900);
        carService.getAllByDriver(morales.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(morales, renaultLogan);
        carService.delete(saab900.getId());
        carService.getAllByDriver(morales.getId()).forEach(System.out::println);
    }
}
