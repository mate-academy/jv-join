package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer(0L, "Audi", "Germany");
        manufacturerService.create(manufacturer);
        List<Driver> drivers = createDrivers();
        drivers.forEach(driverService::create);
        Car car = carService.create(new Car(0L, "A4", manufacturer, List.of()));
        car.setDrivers(drivers);
        carService.update(car);
        Driver driver = drivers.get(new Random().nextInt(drivers.size()));
        carService.getAllByDriver(driver.getId());
        carService.removeDriverFromCar(driver, car);
        carService.addDriverToCar(driver, car);
        carService.getAll();
        carService.get(car.getId());
        carService.delete(car.getId());
    }

    private static List<Driver> createDrivers() {
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(0L, "Alice", "aa1111"));
        drivers.add(new Driver(0L, "Bob", "bb2222"));
        drivers.add(new Driver(0L, "Charles", "cc3333"));
        return drivers;
    }
}
