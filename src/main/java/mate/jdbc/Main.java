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

    public static void main(String[] args) {
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver tatsu = new Driver("Tatsu", "123456");
        driverService.create(tatsu);
        Driver sakuta = new Driver("Sakuta", "789012");
        driverService.create(sakuta);
        Driver denji = new Driver("Denji", "345678");
        driverService.create(denji);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(toyota);
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(audi);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car toyotaCorolla = new Car("Toyota Corolla", toyota, List.of(tatsu, sakuta));
        carService.create(toyotaCorolla);
        Car toyotaYaris = new Car("Toyota Yaris", toyota, List.of(tatsu, denji));
        carService.create(toyotaYaris);
        Car audiQ7 = new Car("Audi Q7", audi, List.of(denji, sakuta));
        carService.create(audiQ7);
        carService.getAll().forEach(System.out::println);
        carService.addDriverToCar(denji, toyotaCorolla);
        System.out.println(toyotaCorolla.getDrivers());
        carService.removeDriverFromCar(denji, toyotaCorolla);
        System.out.println(toyotaCorolla.getDrivers());
        carService.delete(toyotaYaris.getId());
        carService.getAll().forEach(System.out::println);
    }
}
