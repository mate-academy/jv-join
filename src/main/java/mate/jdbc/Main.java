package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Car car = new Car();
        Manufacturer manufacturer = new Manufacturer(77L,"Ferrari","Italy");
        List<Driver> drivers = new ArrayList<>();
        Driver driver = new Driver(54L,"Vasyl","001");
        drivers.add(driver);
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        carService.update(car);
        carService.delete(car.getId());
    }
}
