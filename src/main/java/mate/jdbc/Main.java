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
        // test your code here
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver sam = new Driver("Sam", "BD-1154");
        Driver alice = new Driver("Alice", "HT-8655");
        Driver anna = new Driver("Anna", "VF-0909");
        driverService.create(sam);
        driverService.create(alice);
        driverService.create(anna);
        List<Driver> drivers = new ArrayList<Driver>();
        drivers.add(driverService.get(sam.getId()));
        drivers.add(driverService.get(alice.getId()));

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer renault = new Manufacturer("Renault", "France");
        manufacturerService.create(toyota);
        manufacturerService.create(renault);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(new Car("Camry", manufacturerService.get(toyota.getId()), drivers));
        carService.create(new Car("Clio", manufacturerService.get(renault.getId()), drivers));

        System.out.println(carService.get(toyota.getId()));//GET CAR BY ID
        carService.addDriverToCar(anna, carService.getAll().get(1));//ADD DRIVER-CAR
        System.out.println(carService.getAllByDriver(sam.getId()));//GET ALL CARS
        carService.delete(carService.getAll().get(0).getId());//MARK CAR AS DELETED
    }
}
