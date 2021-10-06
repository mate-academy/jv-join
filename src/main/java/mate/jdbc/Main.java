package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Car car = new Car();
        car.setModel("Panamera");
        car.setManufacturer(manufacturerService.get(2L));
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverAlice = driverService.get(5L);
        Driver driverJohn = driverService.get(6L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverAlice);
        drivers.add(driverJohn);
        car.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.getAll().forEach(System.out::println);
        System.out.println("create car: ");
        System.out.println(carService.create(car));
        System.out.println("get car:");
        System.out.println(carService.get(car.getId()));
        System.out.println("get all cars:");
        carService.getAll().forEach(System.out::println);
        System.out.println("update car: model - Lanos, add driverBob, manufacturer id - 1");
        car.setModel("Lanos");
        Driver driverBob = driverService.get(4L);
        car.getDrivers().add(driverBob);
        car.setManufacturer(manufacturerService.get(1L));
        System.out.println(carService.update(car));
        carService.get(car.getId());
        System.out.println("delete driverAlice and DriverBob:");
        carService.removeDriverFromCar(driverAlice, car);
        carService.removeDriverFromCar(driverBob, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("add driverBob:");
        carService.addDriverToCar(driverBob, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("add all cars by driverAlice:");
        carService.getAllByDriver(driverAlice.getId()).forEach(System.out::println);
        System.out.println("delete car:");
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
    }
}
