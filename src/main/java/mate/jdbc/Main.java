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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer volkswagen = new Manufacturer("Volkswagen", "Germany");
        manufacturerService.create(volkswagen);
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(toyota);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver("John", "111");
        driverService.create(firstDriver);
        Driver secondDriver = new Driver("Bob", "222");
        driverService.create(secondDriver);

        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car amarok = new Car("Amarok", volkswagen, new ArrayList<>());
        carService.create(amarok);
        Car corolla = new Car("Corolla RAV4", bmw, new ArrayList<>());
        carService.create(corolla);
        Car m4 = new Car("M4", bmw, new ArrayList<>());
        carService.create(m4);

        carService.addDriverToCar(firstDriver, amarok);
        carService.addDriverToCar(firstDriver, m4);
        carService.addDriverToCar(secondDriver, corolla);
        carService.getAllCarsByDriver(firstDriver.getId()).forEach(System.out::println);
        carService.getAllCarsByDriver(secondDriver.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(firstDriver, m4);
        carService.getAllCarsByDriver(firstDriver.getId()).forEach(System.out::println);
    }
}
