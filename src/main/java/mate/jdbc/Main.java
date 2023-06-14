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
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        final CarService carService
                = (CarService) injector.getInstance(CarService.class);
        final Manufacturer manufacturer = new Manufacturer("LANOS", "UKRAINE");
        manufacturerService.create(manufacturer);

        Driver stepan = new Driver("Stepan", "123");
        Driver petro = new Driver("Petro", "321");
        driverService.create(stepan);
        driverService.create(petro);
        System.out.println(driverService.getAll());

        Car car = new Car("Mitsubishi", manufacturer, List.of(stepan, petro));
        car.setModel("BMW");
        car = carService.create(car);
        car = carService.get(car.getId());

        carService.removeDriverFromCar(petro, car);

        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(stepan.getId()).forEach(System.out::println);

        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
    }
}
