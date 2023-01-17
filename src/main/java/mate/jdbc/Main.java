package mate.jdbc;

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

        Manufacturer manufacturerFirst = new Manufacturer(null, "Renault", "France");
        Manufacturer manufacturerSecond = new Manufacturer(null, "Skoda", "Czech Republic");
        Manufacturer manufacturerThird = new Manufacturer(null, "Hyundai", "South Korea");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturerFirst);
        manufacturerService.create(manufacturerSecond);
        manufacturerService.create(manufacturerThird);
        Driver driverFirst = new Driver(null, "Johnny Cash","3094410076");
        Driver driverSecond = new Driver(null, "Johnny Depp","3094410077");
        Driver driverThird = new Driver(null, "Peter Griffin","3094410032");
        Driver driverFourth = new Driver(null, "Sarah Connor","3094410033");
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverFirst);
        driverService.create(driverSecond);
        driverService.create(driverThird);
        driverService.create(driverFourth);
        Car carFirst = new Car(null,
                "Logan", manufacturerFirst, List.of(driverFirst));
        Car carSecond = new Car(null,
                "Scala", manufacturerSecond, List.of(driverThird, driverFourth));
        Car carThird = new Car(null,
                "Accent", manufacturerThird, List.of(driverFirst, driverFourth));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carFirst);
        carService.create(carSecond);
        carService.create(carThird);
        System.out.println(carService.get(2L));
        System.out.println(carService.getAllByDriver(1L));
        carService.addDriverToCar(driverSecond, carFirst);
        System.out.println(carService.getAll());
        carService.removeDriverFromCar(driverSecond, carFirst);
        System.out.println(carService.getAll());
        carService.delete(1L);
    }
}
