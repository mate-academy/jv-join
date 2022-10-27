package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Driver> drivers = driverService.getAll();
        System.out.println(carService.get(3L));
        System.out.println(carService.getAll());
        carService.update(new Car(3L, "focus", manufacturerService.get(1L), drivers));
        System.out.println(carService.delete(1L));
        System.out.println(carService.getAllByDriver(1L));
        carService.addDriverToCar(driverService.get(1L), carService.get(5L));
        carService.removeDriverFromCar(driverService.get(1L), carService.get(5L));
    }
}
