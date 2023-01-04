package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Car car = new Car();
        car.setModel("X5");
        Manufacturer manufacturer = new Manufacturer(
                1L, "BMW", "Germany"
        );
        car.setManufacturer(manufacturer);
        Driver bob = new Driver(1L, "Bob", "12345");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        car.setDrivers(drivers);
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        carService.create(car);
        carService.get(car.getId());
        carService.getAll();
        car.setModel("X5s");
        carService.update(car);
        Driver alice = new Driver(2L, "Alice", "6789");
        carService.addDriverToCar(alice, car);
        carService.removeDriverFromCar(bob, car);
        carService.delete(car.getId());
    }
}
