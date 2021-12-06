package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.DriverServiceImpl;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer manufacturer = new Manufacturer("BMW", "GERMANY");
        Car car = new Car();
        car.setManufacturer();
        carService.create()
    }
}