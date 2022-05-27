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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        manufacturerService.create(bmw);
        Driver bob = new Driver("Bob", "1231313");
        driverService.create(bob);
        List<Driver> drivers = new ArrayList<>();
        Car car = new Car("I3", bmw, drivers);
        carService.create(car);
        System.out.println(carService.getAll());
        carService.addDriverToCar(bob, car);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(bob, car);
        car.setModel("I5");
        System.out.println(carService.get(car.getId()));
        carService.update(car);
        carService.delete(car.getId());
    }
}
