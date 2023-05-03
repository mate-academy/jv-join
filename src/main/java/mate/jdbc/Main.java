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
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer =
                manufacturerService.create(new Manufacturer("manufacturerA", "manufacturerAA"));
        driverService.create(new Driver("driverA", "driverAA"));
        driverService.create(new Driver("driverB", "driverBB"));
        driverService.create(new Driver("driverC", "driverCC"));
        driverService.create(new Driver("driverD", "driverDD"));
        List<Driver> drivers = driverService.getAll();
        Car car = carService.create(new Car("car1", manufacturer, drivers));
        System.out.println(carService.create(car));
        System.out.println(carService.get(1L));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(1L));
        Driver driverE = new Driver("driverE", "driverEE");
        drivers.add(driverService.create(driverE));
        car.setDrivers(drivers);
        System.out.println(carService.update(car));
        carService.delete(car.getId());
    }
}
