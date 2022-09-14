package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;


public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.get(1L));
    }
}
