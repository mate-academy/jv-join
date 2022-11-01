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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        manufacturerService.create(new Manufacturer("BMW", "Germany"));
        carService.getAll().forEach(System.out::println);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.create(new Driver("Petro", "UA1234567")));
        drivers.add(driverService.create(new Driver("Pavlo", "UA3331115")));
        Car car = new Car("CRV",
                manufacturerService.create(new Manufacturer("Honda", "Japan")));
        car.setDrivers(drivers);
        carService.create(car);
        System.out.println(carService.get(car.getId()).toString());
        car.setModel("Accord");
        System.out.println(carService.update(car).toString());
        Driver driver = driverService.create(new Driver("Sam", "USA333333"));
        carService.addDriverToCar(driver, car);
        System.out.println(carService.getAllByDriver(driver.getId()).toString());
        carService.removeDriverFromCar(driver, car);
        carService.delete(car.getId());
    }
}
