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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer("Honda", "Japan");
        manufacturer = manufacturerService.create(manufacturer);
        Driver bob = new Driver("Bob", "12345");
        bob = driverService.create(bob);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car = new Car("fast", manufacturer, drivers);
        car = carService.create(car);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        Driver alice = new Driver("Alice", "54321");
        alice = driverService.create(alice);
        carService.addDriverToCar(alice, car);
        carService.removeDriverFromCar(bob, car);
        cars = carService.getAll();
        cars.forEach(System.out::println);
        carService.delete(car.getId());
        cars = carService.getAll();
        cars.forEach(System.out::println);
    }
}
