package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.get(4L));
        //driverService.create(new Driver(3L, "Vadim", "3333333"));
        //carService.addDriverToCar(driverService.get(3L), carService.get(4L));
        carService.removeDriverFromCar(driverService.get(3L), carService.get(4L));
    }
}
