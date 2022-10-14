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
        driverService.create(new Driver("Ivan Salo", "CD123456"));
        driverService.getAll().forEach(System.out::println);
        //System.out.println(driverService.get(4L));
        driverService.delete(3L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.create(new Driver(null, "Name Surname", "AB123456")));
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.getAll().forEach(System.out::println);
        Manufacturer manufacturer = manufacturerService.get(24L);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(new Car("Nissan", manufacturer, drivers));
        carService.getAll().forEach(System.out::println);
        carService.update(new Car(7L, "Renault", manufacturer, drivers));
        System.out.println(carService.get(1L));
        carService.addDriverToCar(driverService.get(30L), carService.get(7L));
        carService.removeDriverFromCar(driverService.get(30L), carService.get(7L));
        System.out.println(carService.getAllByDriver(5L));
        carService.getAll().forEach(System.out::println);
        driverService.update(new Driver(27L, "Ivan Salo", "CD123456"));
    }
}
