package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = List.of(
                new Manufacturer("GM", "USA"),
                new Manufacturer("BMW", "Germany"));
        manufacturers.forEach(manufacturerService::create);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = List.of(
                new Driver("Schumacher", "01"),
                new Driver("Hamilton", "02"),
                new Driver("Russell", "03"));
        drivers.forEach(driverService::create);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = List.of(
                new Car("Challenger", manufacturers.get(0), drivers),
                new Car("530d", manufacturers.get(1), drivers));
        //create
        cars.forEach(car -> {
            car = carService.create(car);
            System.out.println(car);
        });

    }
}
