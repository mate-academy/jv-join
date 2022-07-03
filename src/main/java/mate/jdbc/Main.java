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
        ManufacturerService manufacturerService = 
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer1 = new Manufacturer("ZAZ", "Ukraine");
        Manufacturer manufacturer2 = new Manufacturer("OPEL", "GERMANY");
        manufacturerService.create(manufacturer1);
        manufacturerService.create(manufacturer2);
        manufacturerService.getAll().forEach(System.out::println);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver("Vova", "123321");
        Driver driver2 = new Driver("Kolya", "777777");
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.getAll().forEach(System.out::println);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = new Car("Vectra", manufacturer1, List.of(driver1, driver2));
        carService.create(car);
        System.out.println(carService.getAllByDriver(1L));
    }
}
