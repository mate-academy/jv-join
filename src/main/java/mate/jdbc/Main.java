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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(2L);
        System.out.println(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(4L));
        drivers.add(driverService.get(5L));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("civic", manufacturer, drivers);
        System.out.println(carService.create(car));
        System.out.println(carService.get(3L));
        System.out.println(carService.delete(10L));
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(4L));
        List<Driver> newDrivers = new ArrayList<>();
        newDrivers.add(driverService.get(6L));
        newDrivers.add(driverService.get(7L));
        Car updateCar = new Car(8L, "TESTINGKK", manufacturer, newDrivers);
        System.out.println(updateCar.getDrivers() + "***");
        System.out.println(carService.update(updateCar));
        carService.addDriverToCar(driverService.get(4L), new Car(6L, "honda"));
        carService.removeDriverFromCar(driverService.get(4L), new Car(17L, "honda"));
    }
}
