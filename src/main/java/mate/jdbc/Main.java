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
        Long testValue = 6L;
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(testValue);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(testValue);
        Driver driver1 = driverService.get(testValue + 3);
        Driver driver3 = driverService.get(testValue + 2);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        drivers.add(driver1);
        Car car = new Car(null, "model" + testValue, manufacturer,
                drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car2 = carService.create(car);
        Car car1 = carService.get(3L);
        carService.addDriverToCar(driver3, car1);
        Car update = carService.update(car1);
        boolean delete = carService.delete(1L);
        List<Car> all = carService.getAll();
        List<Car> allByDriver = carService.getAllByDriver(3L);
        carService.removeDriverFromCar(driver, car1);
    }
}
