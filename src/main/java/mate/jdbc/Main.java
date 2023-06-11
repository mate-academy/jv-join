package mate.jdbc;

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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer mersedes = new Manufacturer("Mersedes", "German");
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(mersedes);
        manufacturerService.create(toyota);
        manufacturerService.getAll().forEach(System.out::println);

        Driver firstDriver = new Driver("Sam", "466574AS");
        driverService.create(firstDriver);
        Driver secondDriver = new Driver("Jon", "87dd788");
        driverService.create(secondDriver);
        driverService.getAll().forEach(System.out::println);

        Car car = new Car("GLE2hCarDao011", mersedes, List.of(firstDriver, secondDriver));
        Car car2 = new Car("RX222", toyota, List.of(firstDriver));
        carService.create(car);
        carService.create(car2);
        System.out.println(carService.get(car2.getId()));
        carService.removeDriverFromCar(firstDriver, car);

        toyota.setName("Lexys");
        manufacturerService.update(toyota);
        manufacturerService.getAll().forEach(System.out::println);

        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.delete(car.getId());
        System.out.println(manufacturerService.get(mersedes.getId()));
        driverService.getAll().forEach(System.out::println);
    }
}
