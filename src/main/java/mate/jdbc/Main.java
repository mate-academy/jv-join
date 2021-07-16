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
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);
    private static CarService carService = (CarService)
            injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer volkswagen = new Manufacturer();
        volkswagen.setName("Volkswagen");
        volkswagen.setCountry("Germany");
        manufacturerService.create(volkswagen);
        Manufacturer volvo = new Manufacturer();
        volvo.setName("Volvo");
        volvo.setCountry("Sweden");
        manufacturerService.create(volvo);
        Manufacturer tesla = new Manufacturer();
        tesla.setName("Tesla");
        tesla.setCountry("USA");
        manufacturerService.create(tesla);
        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("SDF2215");
        driverService.create(bob);
        Driver linda = new Driver();
        linda.setName("Linda");
        linda.setLicenseNumber("DFG6581");
        driverService.create(linda);
        Driver tom = new Driver();
        tom.setName("Tom");
        tom.setLicenseNumber("YUI1544");
        driverService.create(tom);

        List<Driver> golfDrivers = new ArrayList<>();
        golfDrivers.add(bob);
        final Car golf = new Car("Golf", volkswagen, golfDrivers);
        List<Driver> passatDrivers = new ArrayList<>();
        passatDrivers.add(bob);
        passatDrivers.add(linda);
        final Car passat = new Car("Passat", volkswagen, passatDrivers);
        List<Driver> modelXdrivers = new ArrayList<>();
        modelXdrivers.add(tom);
        modelXdrivers.add(linda);
        modelXdrivers.add(bob);
        final Car modelX = new Car("Model X", volkswagen, modelXdrivers);
        carService.create(golf);
        carService.create(passat);
        carService.create(modelX);
        carService.getAll().forEach(System.out::println);

        carService.getAllByDriver(tom.getId()).forEach(System.out::println);
        carService.addDriverToCar(tom, golf);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(bob,golf);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
    }
}
