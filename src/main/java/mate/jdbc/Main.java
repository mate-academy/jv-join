package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = carService.get(7L);
        System.out.println(car);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver anton = driverService.get(7L);

        carService.addDriverToCar(anton, car);

        System.out.println(car);

        carService.removeDriverFromCar(anton, car);
        System.out.println(car);

        for (Car car3 : carService.getAll()) {
            System.out.println(car3);
        }

    }
}
