package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector;
    private static ManufacturerService manufacturerService;
    private static DriverService driverService;
    private static CarService carService;

    public static void main(String[] args) {
        injector = Injector.getInstance("mate.jdbc");
        manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        driverService = (DriverService) injector.getInstance(DriverService.class);
        carService = (CarService) injector.getInstance(CarService.class);

    }
}
