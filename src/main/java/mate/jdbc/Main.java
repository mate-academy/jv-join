package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService)
                injector.getInstance(CarServiceImpl.class);
        carService.getAllByDriver(3L).forEach(System.out::println);
    }
}
