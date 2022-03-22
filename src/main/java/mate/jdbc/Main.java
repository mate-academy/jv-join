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
        driversMain.add(firstDriver);
        Manufacturer manufacturer = manufacturerService.create(new Manufacturer("General Motors","USA"));
        Car car = carService.create(new Car("Hummer H3", manufacturer, driversMain));
        carService.get(car.getId());
        carService.getAll();
        car.setModel("Corvette");
        manufacturer.setName("Chevrolet");
        carService.update(car);
        carService.getAllByDriver(firstDriver.getId());
        Driver secondDriver = driverService.create(new Driver("Joseph", "6-21-1788"));
        carService.addDriverToCar(secondDriver, car);
        carService.removeDriverFromCar(secondDriver, car);
        System.out.println(carService.delete(car.getId()));
    }
}
