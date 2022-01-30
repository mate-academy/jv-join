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
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        audi = manufacturerService.create(audi);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver roman = new Driver("Roman", "1234");
        roman = driverService.create(roman);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(roman);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("RX7", audi, drivers);
        car = carService.create(car);
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);

        Driver stepan = new Driver("Stepan", "9876");
        stepan = driverService.create(stepan);
        carService.addDriverToCar(stepan, car);
        carService.removeDriverFromCar(roman, car);

        cars = carService.getAll();
        cars.forEach(System.out::println);
        carService.delete(car.getId());
        cars = carService.getAll();
        cars.forEach(System.out::println);
        List<Car> allByDriver = carService.getAllByDriver(stepan.getId());
        System.out.println(allByDriver);
    }
}
