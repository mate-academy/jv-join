package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        System.out.println(carService.getAll());

    }
}
