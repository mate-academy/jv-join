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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        ManufacturerService manufacturerService = (ManufacturerService) INJECTOR
                .getInstance(ManufacturerService.class);
        Manufacturer carAudi = new Manufacturer(null, "Audi", "Germany");
        Manufacturer carToyota = new Manufacturer(null, "Toyota", "Japan");
        Manufacturer manufacturerAudi =
                manufacturerService.create(carAudi);
        Manufacturer manufacturerToyota =
                manufacturerService.create(carToyota);
        Driver driver1 = new Driver(null, "Bobik", "1342334");
        Driver driver2 = new Driver(null, "Vasil", "1443232");
        Driver driver = driverService.create(driver1);
        Driver driver3 = driverService.create(driver2);
        System.out.println(driver);
        System.out.println(driver3);
        List<Driver> drivers = new ArrayList<>();
        Car car = new Car("Audi", manufacturerAudi, drivers);
        Car car1 = new Car("Toyota", manufacturerToyota, null);
        Car car2 = carService.create(car);
        Car car3 = carService.create(car1);
        carService.addDriverToCar(driver, car);
        List<Car> allByDriver = carService.getAllByDriver(driver.getId());
        carService.removeDriverFromCar(driver, car);

    }
}
