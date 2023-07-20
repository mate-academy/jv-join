package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService
            = (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        carService.getAll();

        carService.get(1L);
    }
}
