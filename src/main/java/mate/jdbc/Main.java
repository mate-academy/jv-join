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
    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer kia = manufacturerService.get(7L);
        Manufacturer skoda = manufacturerService.get(8L);
        Manufacturer daewoo = manufacturerService.get(9L);
        Car logan = new Car("rio", kia);
        Car oktavia = new Car("oktavia", skoda);
//        System.out.println(carService.create(logan));
//        carService.create(oktavia);
//        System.out.println(carService.get(1L));
//        System.out.println(carService.get(2L));
//        List<Car> cars = carService.getAll();
//        System.out.println(cars);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(2L));
        drivers.add(driverService.get(3L));
        Car lanos = new Car(1L,"lanos", daewoo, drivers);
//        Car update = carService.update(lanos);
//        System.out.println(update);
    }
}
