package mate.jdbc;

import java.sql.SQLOutput;
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

        Driver vasil = new Driver("Vasil", "fs5ss8");
        Driver yuriy = new Driver("Yuriy", "s6s5d4");
        Driver anna = new Driver("Anna", "asd5s1");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(yuriy);
        driverService.create(vasil);
        driverService.create(anna);

        Car lanos = new Car("lanos", zaz);
        Car sens = new Car("sens", zaz);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(lanos);
        carService.create(sens);
        carService.addDriverToCar(vasil, sens);
        carService.addDriverToCar(vasil, lanos);
        carService.addDriverToCar(anna, sens);
        carService.addDriverToCar(anna, lanos);
        carService.addDriverToCar(yuriy, sens);

        System.out.println("**********");
        System.out.println("Change model lanos");
        System.out.println(carService.get(lanos.getId()));
        lanos.setModel("lanos sx");
        carService.update(lanos);
        System.out.println(carService.get(lanos.getId()));
        System.out.println("**********");
        System.out.println("Get all cars");
        System.out.println(carService.getAll());
        System.out.println("**********");
        System.out.println("Print all drivers after add");
        carService.getAllByDriver(vasil.getId()).forEach(System.out::println);
        System.out.println("**********");
        System.out.println("Remove some drivers");
        carService.removeDriverFromCar(vasil, sens);
        carService.getAllByDriver(vasil.getId()).forEach(System.out::println);





    }
}
