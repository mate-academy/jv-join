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
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.get(6L));
        List<Driver> drivers = new ArrayList<>();
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (
                ManufacturerService) injector.getInstance(ManufacturerService.class);
        drivers.add(driverService.get(7L));
        drivers.add(driverService.get(8L));
        Manufacturer manufacturer = manufacturerService.get(8L);
        Car car = new Car("Mustang", manufacturer, drivers);
        Car savedCar = carService.create(car);
        System.out.println("created car:");
        System.out.println(savedCar);
        carService.addDriverToCar(driverService.get(9L), savedCar);
        carService.removeDriverFromCar(driverService.get(7L), savedCar);
        savedCar.setModel("Fusion");
        System.out.println("modified car:");
        System.out.println(carService.update(savedCar));
        System.out.println("getAll:");
        carService.getAll().forEach(System.out::println);
        System.out.println("delete:");
        System.out.println(carService.delete(savedCar.getId()));
        System.out.println("getAll after delete:");
        carService.getAll().forEach(System.out::println);
        System.out.println("getAllByDriver:");
        System.out.println(carService.getAllByDriver(7L));
    }
}
