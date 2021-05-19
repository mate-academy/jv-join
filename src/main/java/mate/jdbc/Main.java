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
        Driver driverOleg = new Driver("Oleg Pavlytskiy", "283475");
        Driver driverTaras = new Driver("Taras Duda", "752403");
        Driver driverRostik = new Driver("Rostik Kuzyk", "552782");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverOleg);
        driverService.create(driverTaras);
        driverService.create(driverRostik);

        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer bmw = new Manufacturer("BMW", "German");
        Manufacturer renault = new Manufacturer("Renault", "France");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(toyota);
        manufacturerService.create(bmw);
        manufacturerService.create(renault);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverOleg);
        drivers.add(driverRostik);
        drivers.add(driverTaras);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car megan = new Car("Megan", drivers,renault);
        Car m5 = new Car("m5", drivers,bmw);
        Car camry = new Car("Camry", drivers,toyota);
        carService.create(megan);
        carService.create(m5);
        carService.create(camry);
        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(driverOleg, camry);
        carService.addDriverToCar(driverRostik, m5);
        carService.removeDriverFromCar(driverOleg, camry);

        carService.getAll().forEach(System.out::println);
    }
}
