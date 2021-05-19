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
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Driver driverJohn = new Driver("John", "32452345");
        Driver driverGeorge = new Driver("George", "879789");
        Driver driverAlex = new Driver("Alex", "15435345");
        Driver driverTom = new Driver("Tom", "54646456");
        Driver driverHarry = new Driver("Harry", "325345345");
        Driver driverOleg = new Driver("Oleg", "6578560975");
        driverService.create(driverJohn);
        driverService.create(driverGeorge);
        driverService.create(driverAlex);
        driverService.create(driverTom);
        driverService.create(driverHarry);
        driverService.create(driverOleg);

        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerAudi = new Manufacturer("Audi","Germany");
        Manufacturer manufacturerMercedes = new Manufacturer("Mercedes","Germany");
        manufacturerService.create(manufacturerAudi);
        manufacturerService.create(manufacturerMercedes);

        List<Driver> mercedesDrivers = new ArrayList<>();
        mercedesDrivers.add(driverJohn);
        mercedesDrivers.add(driverGeorge);

        List<Driver> audiDrivers = new ArrayList<>();
        audiDrivers.add(driverJohn);
        audiDrivers.add(driverAlex);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverHarry);
        drivers.add(driverOleg);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carAudi = new Car(manufacturerAudi, "A4");
        carAudi.setDrivers(audiDrivers);
        carService.create(carAudi);

        Car mercedes = new Car(manufacturerMercedes, "E-CLASS");
        mercedes.setDrivers(mercedesDrivers);
        System.out.println(carService.create(mercedes));
        carService.addDriverToCar(driverHarry, mercedes);

        System.out.println(carService.get(1L));
        carService.update(mercedes);
        System.out.println(carService.getAllByDriver(10L));
        System.out.println(carService.getAll());
        System.out.println(carService.getAll());
        System.out.println("______________");
        System.out.println(carService.getAllByDriver(10L));
    }
}
