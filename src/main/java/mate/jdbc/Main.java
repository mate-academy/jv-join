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
        Driver vasya = new Driver("Vasya", "123456");
        Car modelA = new Car("12-A");
        Car modelB = new Car("12-B");
        List<Driver> drivers = new ArrayList<>();
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        drivers.add(driverService.create(vasya));
        modelA.setDrivers(drivers);
        modelB.setDrivers(drivers);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer citroen = new Manufacturer("Citroen", "Finland");
        modelA.setManufacturer(manufacturerService.create(citroen));
        modelB.setManufacturer(manufacturerService.create(citroen));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(modelB);
        carService.create(modelA);
        System.out.println(modelA);
        Driver petya = new Driver("Petya", "987456");
        carService.addDriverToCar(driverService.create(petya), modelA);
        for (Car car : carService.getAllByDriver(vasya.getId())) {
            System.out.println(car);
        }
        System.out.println(modelA);
        carService.removeDriverFromCar(vasya, modelA);
        System.out.println(modelA);
    }
}
