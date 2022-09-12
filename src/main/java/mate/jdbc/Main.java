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
    private static final String PACKAGE = "mate.jdbc";
    private static final Injector injector = Injector.getInstance(PACKAGE);

    public static void main(String[] args) {
        /*MANUFACTURER*/
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer caterpillar = new Manufacturer(null, "Caterpillar", "USA");
        manufacturerService.create(caterpillar);
        Manufacturer tesla = new Manufacturer(null, "Tesla", "USA");
        manufacturerService.create(tesla);
        Manufacturer banderoMachine = new Manufacturer(null, "Bandero Machine", "Ukraine");
        manufacturerService.create(banderoMachine);

        /*DRIVERS*/
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver catDriver = new Driver(null, "Petro", "1ff5og");
        List<Driver> catDrivers = new ArrayList<>();
        driverService.create(catDriver);
        catDrivers.add(catDriver);

        Driver teslaDriver = new Driver(null, "Galyna", "12t3g2");
        List<Driver> teslaDrivers = new ArrayList<>();
        driverService.create(teslaDriver);
        catDrivers.add(teslaDriver);

        Driver banderoCarDriver1 = new Driver(null, "Dmytro", "12hh54");
        Driver banderoCarDriver2 = new Driver(null, "Anna", "12662h");
        List<Driver> banderoMachineDrivers = new ArrayList<>();
        driverService.create(banderoCarDriver1);
        catDrivers.add(banderoCarDriver1);
        driverService.create(banderoCarDriver2);
        catDrivers.add(banderoCarDriver2);

        /*CAR SERVICE*/
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car teslaCar = new Car(null, "Sedan", tesla, teslaDrivers);
        carService.create(teslaCar);
        Car banderoCar = new Car(null, "Pickup", banderoMachine, banderoMachineDrivers);
        carService.create(banderoCar);
        Car catCar = new Car(null, "Off-highway Truck", caterpillar, catDrivers);
        carService.create(catCar);

        System.out.println("All data from DB");
        System.out.println(carService.getAll());

        System.out.println("List of cars by driver id");
        carService.getAllByDriver(banderoCarDriver1.getId())
                .forEach(System.out::println);

        System.out.println("Adding new owner of car");
        carService.addDriverToCar(catDriver, teslaCar);
        System.out.println(carService.get(teslaCar.getId()));

        System.out.println("Soft-delete tesla car");
        System.out.println("Is deleted-> " + carService.delete(teslaCar.getId()));
    }
}
