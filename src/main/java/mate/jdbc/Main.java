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
        Driver driverOne = driverService.get(1L);
        drivers.add(driverOne);
        Driver driverTwo = driverService.get(2L);
        drivers.add(driverTwo);
        Car car = new Car();
        car.setModel("testModel");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(18L);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.getAll());

        carService.delete(car.getId());
        car = carService.get(1L);
        drivers.remove(1);
        drivers.add(driverService.get(1L));
        car.setDrivers(drivers);
        car.setModel("new X1");
        car = carService.update(car);
        System.out.println(car);

        System.out.println("Cars by driver:");
        List<Car> allByDriver = carService.getAllByDriver(2L);
        allByDriver.stream().forEach(System.out::println);
    }
}
