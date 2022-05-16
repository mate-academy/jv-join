package mate.jdbc;

import java.util.ArrayList;
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
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        final DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        final Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");
        final Driver driverJohn = new Driver("John", "210AVC5");
        final Driver driverKarl = new Driver("Karl", "GH880LP");
        final Driver driverMark = new Driver("Mark", "DFR82DD");
        final Car carFordAspire = new Car("Aspire", manufacturerFord, new ArrayList<>());
        final Car carFordFigo = new Car("Figo", manufacturerFord, new ArrayList<>());
        final Car carFordSport = new Car("Sport", manufacturerFord, new ArrayList<>());

        manufacturerService.create(manufacturerFord);
        carService.create(carFordAspire);
        carService.create(carFordFigo);
        carService.create(carFordSport);
        driverService.create(driverJohn);
        driverService.create(driverKarl);
        driverService.create(driverMark);
        System.out.println(manufacturerService.getAll());
        System.out.println(carService.getAll());
        System.out.println(driverService.getAll());
        carService.addDriverToCar(driverJohn, carFordAspire);
        carService.addDriverToCar(driverJohn, carFordSport);
        carService.addDriverToCar(driverKarl, carFordFigo);
        carService.addDriverToCar(driverKarl, carFordAspire);
        carService.addDriverToCar(driverMark, carFordSport);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(driverJohn.getId()));
        carService.removeDriverFromCar(driverJohn, carFordAspire);
        System.out.println(carService.get(carFordAspire.getId()));
        System.out.println(carService.delete(carFordFigo.getId()));
    }
}
