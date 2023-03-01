package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.DriverServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static final Long LONG_FOR_TEST = 1L;

    public static void main(String[] args) {
        CarServiceImpl carService = (CarServiceImpl) injector.getInstance(CarService.class);
        DriverServiceImpl driverService =
                (DriverServiceImpl) injector.getInstance(DriverService.class);
        System.out.println(carService.getAllByDriver(LONG_FOR_TEST));
        Car car = carService.get(LONG_FOR_TEST);
        Driver driver = driverService.get(LONG_FOR_TEST);
        carService.addDriverToCar(driver, car);
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.getAll());
        System.out.println(carService.create(car));
        System.out.println(carService.update(car));
        System.out.println(carService.getAll());
        System.out.println(carService.get(LONG_FOR_TEST));
        carService.delete(LONG_FOR_TEST);
        System.out.println(carService.getAll());
    }
}
