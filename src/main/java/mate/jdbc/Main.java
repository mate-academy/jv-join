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
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);
    private static final Manufacturer manufacturer = new Manufacturer("BMW", "USA");

    public static void main(String[] args) {
        manufacturerService.create(manufacturer);
        Driver firstDriver = new Driver("Alex", "123");
        Driver seconfDriver = new Driver("Pavel", "456");

        driverService.create(firstDriver);
        driverService.create(seconfDriver);
        System.out.println(driverService.getAll());

        Car car = new Car("Mercedes", manufacturer, List.of(firstDriver, seconfDriver));
        car.setModel("Skoda");

        car = carService.get(car.getId());
        carService.addDriverToCar(firstDriver,car);
        carService.removeDriverFromCar(seconfDriver, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(firstDriver.getId()).forEach(System.out::println);
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
    }
}
