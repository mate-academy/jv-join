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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver stanDriver = new Driver("Stan", "0987");
        Driver mikeDriver = new Driver("Mike", "6543");
        List<Driver> listDrivers = new ArrayList<>();
        listDrivers.add(stanDriver);
        driverService.create(stanDriver);
        driverService.create(mikeDriver);
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer skodaManufacturer = new Manufacturer("Skoda", "Czech");
        manufacturerService.create(skodaManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car fabiaCar = new Car("Fabia", skodaManufacturer, listDrivers);
        Car octaviaCar = new Car("Octavia", skodaManufacturer, listDrivers);
        carService.create(fabiaCar);
        carService.create(octaviaCar);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(fabiaCar.getId()));
        Car feliciaCar = new Car("Felicia", skodaManufacturer, listDrivers);
        feliciaCar.setId(fabiaCar.getId());
        carService.update(feliciaCar);
        carService.delete(octaviaCar.getId());
        carService.addDriverToCar(mikeDriver, feliciaCar);
        System.out.println("List of cars after update, delete and addDriverToCar:");
        carService.getAll().forEach(System.out::println);
        System.out.println("List of cars by driver id:");
        carService.getAllByDriver(stanDriver.getId()).forEach(System.out::println);
        carService.removeDriverFromCar(mikeDriver, octaviaCar);
        System.out.println("List of cars after remove driver from car:");
        carService.getAll().forEach(System.out::println);

    }
}
