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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer lada = new Manufacturer("Lada", "Ukraine");
        Manufacturer deo = new Manufacturer("Deo", "Ukraine");
        manufacturerService.create(lada);
        manufacturerService.create(deo);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver driver = new Driver("John","239056");
        driverService.create(driver);
        List<Driver> driverList = new ArrayList<>();
        driverList.add(driver);
        Car car1 = new Car("VAZ-2107", lada);
        Car car2 = new Car("DEO-Nexia", deo);
        car1.setDrivers(driverList);
        car2.setDrivers(driverList);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        carService.create(car2);
        carService.get(car1.getId());
        carService.delete(car2.getId());
        car1.setModel("VAZ-2108");
        carService.update(car1);
        carService.addDriverToCar(driverService.get(driver.getId()), car1);
        carService.removeDriverFromCar(driverService.get(driver.getId()), car2);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(driver.getId()).forEach(System.out::println);
    }
}
