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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(1L);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(3L);
        Driver driver1 = driverService.get(2L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        Car car = new Car(6L, "Escape", manufacturer,drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        carService.addDriverToCar(driver1, car);
        carService.get(6L);
        carService.getAll();
        carService.update(car);
        carService.delete(3L);
        carService.removeDriverFromCar(driver,car);
        carService.getAllByDriver(2L);
    }
}
