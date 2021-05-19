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
        Manufacturer zaz = new Manufacturer("ZAZ", "Ukraine");
        Manufacturer skoda = new Manufacturer("Skoda", "Czech");
        manufacturerService.create(zaz);
        manufacturerService.create(skoda);
        
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        Driver mike = new Driver("Mike", "KX010101");
        Driver travis = new Driver("Travis", "KX020202");
        driverService.create(mike);
        driverService.create(travis);
    
        Car lanos = new Car("Lanos", zaz);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(mike);
        drivers.add(travis);
        lanos.setDrivers(drivers);
        Car octavia = new Car("Octavia", skoda);
        CarService carService = (CarService) injector.getInstance(CarService.class);
    
        System.out.println(carService.create(lanos));
        System.out.println(carService.create(octavia));
        System.out.println(carService.get(1L));
        lanos.setManufacturer(skoda);
        System.out.println(carService.update(lanos));
        System.out.println(carService.delete(2L));
        carService.addDriverToCar(mike, octavia);
        carService.addDriverToCar(travis, octavia);
        carService.removeDriverFromCar(mike, lanos);
        System.out.println(carService.getAll());
        System.out.println(carService.getAllByDriver(1L));
    }
}
