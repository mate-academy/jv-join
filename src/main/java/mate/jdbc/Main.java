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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer(null, "BMW Group", "German");
        manufacturer = manufacturerService.create(manufacturer);
        Driver driver1 = new Driver(null, "Erik Bush", "9078");
        Driver driver2 = new Driver(null, "Kate Russel", "5678");
        driver1 = driverService.create(driver1);
        driver2 = driverService.create(driver2);
        Car car = new Car();
        car.setModel("BMM");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driver1, driver2));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("BMW");
        car = carService.update(car);
        carService.addDriverToCar(driver2, car);
        carService.removeDriverFromCar(driver2, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
