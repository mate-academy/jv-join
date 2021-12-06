package mate.jdbc;

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
        Manufacturer zaz = new Manufacturer("Zaz", "Ukraine");
        manufacturerService.create(zaz);

        Car lanos = new Car("lanos", zaz);
        Car sens = new Car("sens", zaz);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(lanos);
        carService.create(sens);

        Driver vasil = new Driver("Vasil", "fs5ss8");
        Driver yuriy = new Driver("Yuriy", "s6s5d4");
        Driver anna = new Driver("Anna", "asd5s1");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(yuriy);
        driverService.create(vasil);
        driverService.create(anna);

        carService.addDriverToCar(vasil, sens);
        carService.addDriverToCar(vasil, lanos);
        carService.addDriverToCar(anna, sens);
        carService.addDriverToCar(anna, lanos);
        carService.addDriverToCar(yuriy, sens);

        carService.getAllByDriver(vasil.getId()).forEach(System.out::println);
        System.out.println("**********");
        carService.removeDriverFromCar(vasil, sens);

        carService.getAllByDriver(vasil.getId()).forEach(System.out::println);

    }
}
