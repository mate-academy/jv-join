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
        System.out.println("All cars:");
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println(System.lineSeparator() + "Car with id 2:");
        Car car = carService.get(2L);
        System.out.println(car);
        System.out.println(System.lineSeparator() + "Add new car:");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService
                .create(new Manufacturer("Peugeot", "France"));
        car = new Car("208", manufacturer);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(3L);
        Driver driver2 = driverService.get(5L);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver2);
        car.setDrivers(drivers);
        System.out.println(carService.create(car));
        System.out.println(System.lineSeparator() + "Delete car with id 4:");
        System.out.println(carService.delete(4L));
        System.out.println(System.lineSeparator() + "Update car with id 3:");
        Car car2 = carService.get(3L);
        car2.setDrivers(drivers);
        System.out.println(carService.update(car2));
        System.out.println(System.lineSeparator() + "All cars:");
        cars = carService.getAll();
        cars.forEach(System.out::println);
        Driver driver3 = driverService.create(new Driver("Alisa", "9743"));
        System.out.println(System.lineSeparator() + "Get all cars by driver with id "
                + driver3.getId() + ":");
        carService.addDriverToCar(driver3, car);
        System.out.println(carService.getAllByDriver(driver3.getId()));
        System.out.println(System.lineSeparator() + "Get current all cars by driver with id "
                + driver3.getId() + ":");
        carService.removeDriverFromCar(driver3, car);
        carService.addDriverToCar(driver3, car2);
        System.out.println(carService.getAllByDriver(driver3.getId()));
    }
}
