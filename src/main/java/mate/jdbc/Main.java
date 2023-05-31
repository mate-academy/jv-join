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
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer manufacturer = new Manufacturer(null, "BMW", "USA");
        manufacturerService.create(manufacturer);
        Driver firstDriver = new Driver(null, "Alex", "123");
        Driver seconfDriver = new Driver(null, "Pavel", "456");
        driverService.create(firstDriver);
        driverService.create(seconfDriver);
        System.out.println(driverService.getAll());
        Car car = new Car();
        car.setModel("Mercedes");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(firstDriver, seconfDriver));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("Skoda");
        carService.addDriverToCar(firstDriver,car);
        carService.removeDriverFromCar(seconfDriver, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(1L);
        carService.getAll().forEach(System.out::println);
    }
}
