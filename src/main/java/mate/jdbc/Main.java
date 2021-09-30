package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
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
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        System.out.println("--------------Test create car----------------");
        Manufacturer manufacturer = manufacturerService.get(9L);
        Driver driverFirst = driverService.get(17L);
        Driver driverSecond = driverService.get(18L);
        Driver driverThird = driverService.get(15L);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setDrivers(new ArrayList<>(Arrays.asList(driverFirst, driverSecond, driverThird)));
        car.setModel("Nubira");
        System.out.println(car);
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(car);
        System.out.println("--------------Test get car by----------------");
        System.out.println(carService.get(car.getId()));
        System.out.println("--------------Test update car----------------");
        car.setId(null);
        carService.create(car);
        car.setModel("Brinson");
        Driver driver = driverService.get(4L);
        car.getDrivers().add(driver);
        carService.update(car);
        System.out.println("--------------Test get all cars----------------");
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Test get all cars by driver----------------");
        System.out.println(carService.getAllByDriver(4L));
        System.out.println("--------------Test delete car----------------");
        car.setId(null);
        carService.create(car);
        carService.getAll().forEach(System.out::println);
        carService.delete(car.getId());
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Test add new driver to car----------------");
        carService.getAll().forEach(System.out::println);
        car.getDrivers().add(driverService.get(7L));
        carService.getAll().forEach(System.out::println);
        System.out.println("--------------Test remove driver to car----------------");
        System.out.println(car.getDrivers().remove(driverService.get(7L)));
        carService.getAll().forEach(System.out::println);
    }
}
