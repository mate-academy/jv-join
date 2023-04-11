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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Driver driver = new Driver(null, "Kopel", "12321");
        Driver driverTwo = new Driver(null, "Oleksii", "1905");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driverTwo);
        Manufacturer manufacturer = new Manufacturer(null, "POLI", "Ukraine");
        System.out.println(manufacturerService.create(manufacturer));
        Car car = new Car(2L, "QWE", manufacturer, drivers);
        System.out.println(carService.delete(1L));
        System.out.println(carService.update(car));
        System.out.println(carService.create(car));
        System.out.println(carService.get(2L));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(2L));
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);
    }
}
