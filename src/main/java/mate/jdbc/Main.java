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

        Manufacturer manufacturerTesla = new Manufacturer(null, "Tesla", "China");
        manufacturerTesla = manufacturerService.create(manufacturerTesla);
        Driver driver1 = new Driver(16L, "Dima", "15565");
        Driver driver2 = new Driver(27L, "Sofiia", "19403");
        driver1 = driverService.create(driver1);
        driver2 = driverService.create(driver2);

        Car car = new Car();
        car.setModel("model 3");
        car.setManufacturer(manufacturerTesla);
        car.setDrivers(List.of(driver1, driver2));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("model 2");
        carService.removeDriverFromCar(driver2, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
