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
        Car car = new Car();
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer(1L, "Nisan", "Japan");
        car.setManufacturer(manufacturerService.create(manufacturer));
        car.setModel("X1");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = new ArrayList<>();
        Driver driverBob = new Driver(1L, "Bob", "qw5890");
        drivers.add(driverService.create(driverBob));
        Driver driverJack = new Driver(1L, "Jack", "15sx8890");
        drivers.add(driverService.create(driverJack));
        Driver driverAlan = new Driver(1L, "Alan", "5sx62830");
        drivers.add(driverService.create(driverAlan));
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("Test carService.create: ");
        System.out.println(carService.create(car));
        Driver driver = driverService.get(driverBob.getId());
        System.out.println("Test carService.get: ");
        System.out.println(car);
        carService.addDriverToCar(driver, car);
        System.out.println("Test carService.addDriverToCar: ");
        System.out.println(car);
        carService.removeDriverFromCar(driver, car);
        System.out.println("Test carService.removeDriverFromCar: ");
        System.out.println(car);
        car.setModel("X3");
        drivers.remove(driverJack);
        System.out.println("Test carService.update: ");
        System.out.println(carService.update(car));
        List<Car> cars = carService.getAllByDriver(driverAlan.getId());
        System.out.println("Test carService.getAllByDriver: ");
        cars.forEach(c -> System.out.println(c.toString()));
        cars = carService.getAll();
        System.out.println("Test carService.getAll: ");
        cars.forEach(c -> System.out.println(c.toString()));
        carService.delete(car.getId());
        cars = carService.getAll();
        System.out.println("Test carService.delete: ");
        cars.forEach(c -> System.out.println(c.toString()));
    }
}
