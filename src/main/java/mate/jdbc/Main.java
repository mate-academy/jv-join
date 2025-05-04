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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Manufacturer manufacturerFord = new Manufacturer(null, "Ford", "USA");
        manufacturerFord = manufacturerService.create(manufacturerFord);
        Driver driver1 = new Driver(1L, "Tom", "221364");
        Driver driver2 = new Driver(2L, "Barbara", "234355");
        Driver driverFirst = driverService.create(driver1);
        Driver driverSecond = driverService.create(driver2);

        Car car = new Car();
        car.setModel("Mondeo");
        car.setManufacturer(manufacturerFord);
        car.setDrivers(List.of(driverFirst, driverSecond));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("F-150");
        carService.removeDriverFromCar(driverSecond, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
