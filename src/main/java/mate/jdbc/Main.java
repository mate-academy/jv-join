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
        Manufacturer opel = new Manufacturer("Opel", "Germany");
        manufacturerService.create(opel);

        Manufacturer fiat = new Manufacturer("Fiat", "France");
        manufacturerService.create(fiat);

        Manufacturer tesla = new Manufacturer("Tesla", "USA");
        manufacturerService.create(tesla);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        List<Driver> teslaDrivers = new ArrayList<>();
        Driver mark = new Driver("Mark", "045678");
        driverService.create(mark);
        teslaDrivers.add(mark);

        Driver john = new Driver("John", "671302");
        driverService.create(john);
        teslaDrivers.add(john);

        List<Driver> opelDrivers = new ArrayList<>();
        Driver victoria = new Driver("Victoria", "357891");
        driverService.create(victoria);
        opelDrivers.add(victoria);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car teslaCar = new Car("tesla", tesla, teslaDrivers);
        carService.create(teslaCar);

        Car opelCar = new Car("Sedan", opel, opelDrivers);
        carService.create(opelCar);

        carService.getAll().forEach(System.out::println);
        System.out.println("Adding driver to car");
        carService.addDriverToCar(victoria, teslaCar);
        System.out.println(carService.get(teslaCar.getId()));
    }
}
