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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer("Tesla", "USA");
        manufacturerService.create(manufacturer);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver mia = new Driver("Mia", "ER1921917");
        driverService.create(mia);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(mia);
        Car car = new Car();
        car.setDrivers(drivers);
        car.setManufacturer(manufacturer);
        car.setModel("TEST MODEL");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));
        System.out.println(carService.get(car.getId()));
        System.out.println(carService.delete(car.getId()));
        System.out.println(carService.update(car));
        carService.removeDriverFromCar(mia, car);
        Driver bob = new Driver("Bob", "ER423423678");
        driverService.create(bob);
        carService.addDriverToCar(bob, car);
        System.out.println(carService.delete(car.getId()));
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
    }
}
