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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(tesla);

        Manufacturer ford = new Manufacturer("Ford", "USA");
        manufacturerService.create(ford);

        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        List<Driver> bmwDrivers = new ArrayList<>();
        Driver misha = new Driver("Mihael", "257254");
        driverService.create(misha);
        bmwDrivers.add(misha);

        Driver timur = new Driver("Timur", "25427522");
        driverService.create(timur);
        bmwDrivers.add(timur);
        List<Driver> teslaDrivers = new ArrayList<>();

        Driver alex = new Driver("Alex", "651698516");
        driverService.create(alex);
        teslaDrivers.add(alex);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmwCar = new Car("BMW", bmw, bmwDrivers);
        carService.create(bmwCar);

        Car teslaCar = new Car("S Plaid", tesla, teslaDrivers);
        carService.create(teslaCar);

        carService.getAll().forEach(System.out::println);

        carService.addDriverToCar(alex, bmwCar);
        System.out.println(carService.get(bmwCar.getId()));
    }
}
