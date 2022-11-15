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
        Long testValue = 5L;
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.get(testValue);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver = driverService.get(testValue);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        Car car = new Car(1L, "model" + testValue, manufacturer,
                drivers);
        Car car2 = carService.create(car);
        Car car1 = carService.get(1L);
        boolean delete = carService.delete(1L);
        List<Car> all = carService.getAll();
        carService.getAll();
        List<Car> allByDriver = carService.getAllByDriver(3L);
        carService.removeDriverFromCar(driver,car1);
        carService.addDriverToCar(driver,car1);
    }
}
