package mate.jdbc;

import mate.jdbc.dao.CarDao;
import mate.jdbc.dao.CarDaoImpl;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;


public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Driver driver1 = new Driver("driver1", "license1");
        Driver driver2 = new Driver("driver2", "license2");
        Driver driver3 = new Driver("driver3", "license3");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driver1 = driverService.create(driver1);
        driver2 = driverService.create(driver2);
        driver3 = driverService.create(driver3);
        Manufacturer manufacturer1 = new Manufacturer("manufacturer1", "country1");
        Manufacturer manufacturer2 = new Manufacturer("manufacturer2", "country2");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturer1 = manufacturerService.create(manufacturer1);
        manufacturer2 = manufacturerService.create(manufacturer2);
        List<Driver> drivers1 = List.of(driver1, driver2);
        List<Driver> drivers2 = List.of(driver2, driver3);
        Car car1 = new Car("model1", manufacturer1, drivers1);
        Car car2 = new Car("model2", manufacturer2, drivers2);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car1 = carService.create(car1);
        System.out.println("Car1: " + car1);
        car2 = carService.create(car2);
        System.out.println("Car2: " + car2);
        System.out.println("Get all cars: " + carService.getAll());
        System.out.println("Get car2: " + carService.get(car2.getId()));
        car2.setModel("model2Updated");
        carService.update(car2);
        System.out.println("Car2 after update: " + carService.get(car2.getId()));
        carService.delete(car1.getId());
        System.out.println("Get all cars after car1 was deleted: " + carService.getAll());
        Driver tmpDriver = new Driver("tmpDriver", "tmpLicense");
        tmpDriver = driverService.create(tmpDriver);
        carService.addDriverToCar(tmpDriver, car2);
        System.out.println("Car2 after tmpDriver was added: " + carService.get(car2.getId()));
        carService.removeDriverFromCar(tmpDriver, car1);
        System.out.println("Car2 after tmpDriver was removed: " + carService.get(car2.getId()));
        System.out.println("Get all cars who use driver2: " + carService.getAllByDriver(driver1.getId()));
        System.out.println("Get all cars who use driver3: " + carService.getAllByDriver(driver3.getId()));

    }
}
