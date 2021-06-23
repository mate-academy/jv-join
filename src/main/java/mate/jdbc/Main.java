package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        Car lada2108 = carService.get(1L);
        carService.removeDriverFromCar(driverService.get(16L), lada2108);
        System.out.println(carService.getAllByDriver(5L));
    }
}
