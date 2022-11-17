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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerFerrari = new Manufacturer("Ferrari","Italy");
        Manufacturer manufacturerBmw = new Manufacturer("BMW","Germany");
        manufacturerService.create(manufacturerFerrari);
        manufacturerService.create(manufacturerBmw);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driverOleh = new Driver("Oleg","777");
        Driver driverVlad = new Driver("Vlad","123");
        Driver driverBohdan = new Driver("Bohdan","543123");
        Driver driverLili = new Driver("Lily","4774");
        driverService.create(driverOleh);
        driverService.create(driverVlad);
        driverService.create(driverBohdan);
        driverService.create(driverLili);
        List<Driver> driversLaFerrari = new ArrayList<>();
        driversLaFerrari.add(driverBohdan);
        List<Driver> driversE34 = new ArrayList<>();
        driversE34.add(driverVlad);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car laFerrari = new Car("LaFerrari",manufacturerFerrari,driversLaFerrari);
        Car e34 = new Car("E34",manufacturerBmw,driversE34);
        carService.create(laFerrari);
        carService.create(e34);
        laFerrari.setModel("f40");
        carService.update(laFerrari);
        driversE34.add(driverLili);
        e34.setDrivers(driversE34);
        carService.update(e34);
        carService.delete(e34.getId());
        carService.removeDriverFromCar(driverVlad,e34);
        carService.addDriverToCar(driverOleh,e34);
        carService.getAllByDriver(driverBohdan.getId());
    }
}
