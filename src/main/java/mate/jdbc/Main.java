package mate.jdbc;

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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        List<Driver> driversMain = new ArrayList<>();
        Driver firstDriver = driverService.create(new Driver("Barack", "7-04-1776"));
        Driver secondDriver = driverService.create(new Driver("Joseph", "6-21-1788"));
        driversMain.add(firstDriver);
        driversMain.add(secondDriver);
        Manufacturer firstManufacturer = manufacturerService.create(new Manufacturer("General Motors",
                "USA"));
        Manufacturer secondManufacturer = manufacturerService.create(new Manufacturer("Nissan",
                "Japan"));
        Car firstCar = carService.create(new Car("Hummer H3", firstManufacturer, driversMain));
        driversMain.remove(firstDriver);
        Car secondCar = carService.create(new Car("X-Trail", secondManufacturer, driversMain));

        System.out.println("Get car from the DB:" + carService.get(firstCar.getId()));
        System.out.println("All cars in the DB: " + carService.getAll());

        secondCar.setModel("Land Cruiser");
        secondManufacturer.setName("Toyota");

        System.out.println("Updated car in the DB: " + carService.update(secondCar));

        System.out.println("All cars in the DB: " + carService.getAll());

        System.out.println("All information for driver " + firstDriver.getName() + ": ");
        carService.getAllByDriver(firstDriver.getId()).forEach(System.out::println);

        carService.addDriverToCar(firstDriver, firstCar);
        carService.removeDriverFromCar(firstDriver, firstCar);
        System.out.println("Car was deleted: " + carService.delete(firstCar.getId()));
    }
}
