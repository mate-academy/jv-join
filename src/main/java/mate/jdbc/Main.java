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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        driverService.getAll().forEach(System.out::println);
        System.out.println(driverService.get(1L));
        driverService.delete(45L);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.create(new Driver("Alex Rudenko", "PA67913")));
        Manufacturer manufacturer = manufacturerService.get(8L);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(new Car("Benz", manufacturer, drivers));

        Car car = new Car();
        car.setModel("Clio");
        car.setId(13L);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        carService.update(car);

        System.out.println(carService.get(1L));
        carService.addDriverToCar(driverService.get(2L), carService.get(5L));
        carService.removeDriverFromCar(driverService.get(1L),carService.get(1L));

        System.out.println(carService.getAllByDriver(10L));
        carService.getAll().forEach(System.out::println);
        driverService.update(new Driver(9L, "Maria Rudenko", "RQ84934"));
    }
}
