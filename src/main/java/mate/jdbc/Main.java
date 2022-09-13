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
        Manufacturer caterpillar = new Manufacturer("Caterpillar", "USA");
        manufacturerService.create(caterpillar);
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(tesla);
        Manufacturer banderoMachine = new Manufacturer("Bandero Machine", "Ukraine");
        manufacturerService.create(banderoMachine);

        /*DRIVERS*/
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> catDrivers = new ArrayList<>();
        Driver catDriver = new Driver("Petro", "1ff5og");
        driverService.create(catDriver);
        catDrivers.add(catDriver);

        List<Driver> teslaDrivers = new ArrayList<>();
        Driver teslaDriver = new Driver("Galyna", "12t3g2");
        driverService.create(teslaDriver);
        teslaDrivers.add(teslaDriver);

        List<Driver> banderoMachineDrivers = new ArrayList<>();
        Driver banderoCarDriver1 = new Driver("Dmytro", "12hh54");
        Driver banderoCarDriver2 = new Driver("Anna", "12662h");
        driverService.create(banderoCarDriver1);
        driverService.create(banderoCarDriver2);
        banderoMachineDrivers.add(banderoCarDriver1);
        banderoMachineDrivers.add(banderoCarDriver2);

        /*CAR SERVICE*/
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car teslaCar = new Car("Sedan", tesla, teslaDrivers);
        carService.create(teslaCar);
        Car banderoCar = new Car("Pickup", banderoMachine, banderoMachineDrivers);
        carService.create(banderoCar);
        Car catCar = new Car("Off-highway Truck", caterpillar, catDrivers);
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
