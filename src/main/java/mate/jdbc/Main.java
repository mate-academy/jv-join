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
    private static final String PACKAGE_NAME = "mate.jdbc";

    public static void main(String[] args) {
        Injector injector = Injector.getInstance(PACKAGE_NAME);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        Manufacturer mazda = new Manufacturer("Mazda", "Japan");
        manufacturerService.create(tesla);
        manufacturerService.create(mazda);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver lisa = new Driver("Lisa", "AA5587");
        Driver nikola = new Driver("Nikola", "JK9866");
        Driver daria = new Driver("Daria", "ID9756");
        driverService.create(lisa);
        driverService.create(nikola);
        driverService.create(daria);
        List<Driver> teslaDrivers = new ArrayList<>();
        teslaDrivers.add(lisa);
        teslaDrivers.add(nikola);
        List<Driver> mazdaDrivers = new ArrayList<>();
        mazdaDrivers.add(daria);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car teslaCar = new Car("Tesla 3", tesla, teslaDrivers);
        Car mazdaCar = new Car("Mazda S", mazda, mazdaDrivers);
        carService.create(teslaCar);
        carService.create(mazdaCar);

        System.out.println(carService.getAll());
        carService.addDriverToCar(nikola, mazdaCar);
        System.out.println(carService.get(mazdaCar.getId()));
    }
}
