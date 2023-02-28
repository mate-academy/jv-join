package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarServiceImpl carService = (CarServiceImpl) injector.getInstance(CarService.class);
        System.out.println(carService.get(6L));
    }
}
