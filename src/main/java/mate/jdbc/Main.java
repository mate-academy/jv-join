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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Driver driverBob = driverService.create(new Driver("Bob", "124512"));
        Driver driverJohn = driverService.create(new Driver("John", "925031"));

        Manufacturer skoda = manufacturerService.create(new Manufacturer("Skoda", "Czech"));
        Manufacturer fiat = manufacturerService.create(new Manufacturer("Fiat", "France"));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverBob);
        drivers.add(driverJohn);

        Car firstCar = new Car("Ducatto", fiat, drivers);
        Car secondCar = new Car("SuperB", skoda, drivers);

        carService.create(firstCar);
        carService.create(secondCar);
        System.out.println(carService.get(secondCar.getId()));

        carService.removeDriverFromCar(driverBob, firstCar);
        manufacturerService.update(skoda);
        manufacturerService.getAll().forEach(System.out::println);

        manufacturerService.getAll().forEach(System.out::println);
        driverService.getAll().forEach(System.out::println);

        List<Car> allByDriver = carService.getAllByDriver(driverBob.getId());
        carService.delete(firstCar.getId());
        Manufacturer manufacturer = manufacturerService.get(fiat.getId());

        System.out.println(allByDriver);
        System.out.println(manufacturer);
        driverService.getAll().forEach(System.out::println);
    }
}
