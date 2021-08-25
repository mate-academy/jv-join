package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        System.out.println("Initial list ******************");
        carService.getAll().forEach(System.out::println);

        System.out.println("1 Car by ID = 1 ***************");
        System.out.println(carService.get(1L));

        System.out.println("2 Cars by driver ID = 4 *******");
        carService.getAllByDriver(4L).forEach(System.out::println);
    }
}
