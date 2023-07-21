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
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Car car = carService.get(2L);
        Driver driver = driverService.get(3L);
        System.out.println("Test carService.get: ");
        System.out.println(car.toString());
        carService.addDriverToCar(driver, car);
        System.out.println("Test carService.addDriverToCar: ");
        System.out.println(car);
        carService.removeDriverFromCar(driver, car);
        System.out.println("Test carService.removeDriverFromCar: ");
        System.out.println(car);
        Car newCar = new Car();
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        newCar.setManufacturer(manufacturerService.get(1L));
        newCar.setModel("X1");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(1L));
        drivers.add(driverService.get(2L));
        drivers.add(driverService.get(3L));
        newCar.setDrivers(drivers);
        System.out.println("Test carService.create: ");
        System.out.println(carService.create(newCar));
        newCar.setModel("X3");
        drivers.remove(2);
        System.out.println("Test carService.update: ");
        System.out.println(carService.update(newCar));
        List<Car> cars = carService.getAllByDriver(2L);
        System.out.println("Test carService.getAllByDriver: ");
        cars.forEach(c -> System.out.println(c.toString()));
        cars = carService.getAll();
        System.out.println("Test carService.getAll: ");
        cars.forEach(c -> System.out.println(c.toString()));
        carService.delete(1L);
        cars = carService.getAll();
        System.out.println("Test carService.delete: ");
        cars.forEach(c -> System.out.println(c.toString()));
    }
}
