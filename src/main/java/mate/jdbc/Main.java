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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        bmw = manufacturerService.create(bmw);
        List<Driver> drivers = new ArrayList<>();
        Driver mia = new Driver("Mia", "12345");
        Driver misha = new Driver("Misha", "567890");
        mia = driverService.create(mia);
        misha = driverService.create(misha);
        drivers.add(mia);
        drivers.add(misha);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car x3 = new Car("X3", bmw, drivers);
        x3 = carService.create(x3);
        System.out.println(carService.get(x3.getId()));
        Driver bob = new Driver("Bob", "09876");
        bob = driverService.create(bob);
        carService.removeDriverFromCar(misha, x3);
        carService.removeDriverFromCar(mia, x3);
        carService.addDriverToCar(bob, x3);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        System.out.println();
        System.out.println(carService.getAllByDriver(bob.getId()));
        System.out.println();
        System.out.println(carService.get(x3.getId()));
        System.out.println(carService.delete(x3.getId()));
    }
}
