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
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(1L));
        drivers.add(driverService.get(2L));
        Car car = new Car();
        car.setModel("testModel");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(18L);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car1 = carService.create(car);
        System.out.println(carService.getAll());

        drivers.remove(1);
        drivers.add(driverService.get(1L));
        car = carService.get(1L);
        car.setDrivers(drivers);
        car.setModel("new X1");
        car = carService.update(car);
        System.out.println(car);
        carService.delete(9L);

        List<Car> allByDriver = carService.getAllByDriver(7L);
        allByDriver.stream().forEach(System.out::println);
    }
}
