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
    private static final DriverService driverService
            = (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService
            = (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        List<Driver> driversMain = new ArrayList<>();
        Driver firstDriver = driverService.create(new Driver("Barack", "7-04-1776"));
        driversMain.add(firstDriver);
        Manufacturer firstManufacturer
                = manufacturerService.create(new Manufacturer("General Motors", "USA"));
        Car firstCar = carService.create(new Car("Hummer H3", firstManufacturer, driversMain));
        carService.getAllByDriver(firstDriver.getId());
        System.out.println("Get first created car from the DB:" + carService.get(firstCar.getId()));
        Driver secondDriver = driverService.create(new Driver("Joseph", "6-21-1788"));
        driversMain.remove(firstDriver);
        driversMain.add(secondDriver);
        Manufacturer secondManufacturer = manufacturerService.create(new Manufacturer("Nissan",
                "Japan"));
        Car secondCar = carService.create(new Car("X-Trail", secondManufacturer, driversMain));
        carService.getAllByDriver(secondDriver.getId());
        System.out.println("Get second created car from the DB:"
                + carService.get(secondCar.getId()));
        System.out.println("All cars in the DB: " + carService.getAll());
        secondCar.setModel("Land Cruiser");
        secondManufacturer.setName("Toyota");
        System.out.println("Updated car in the DB: " + carService.update(secondCar));
        carService.addDriverToCar(firstDriver, secondCar);
        carService.removeDriverFromCar(secondDriver, secondCar);
        System.out.println("Car" + firstCar.getModel() + " was deleted: "
                + carService.delete(firstCar.getId()));
        System.out.println("Car" + secondCar.getModel() + " was deleted: "
                + carService.delete(secondCar.getId()));
    }
}
