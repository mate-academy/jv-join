package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
       // System.out.println(carService.get(2L));
       // carService.getAll().forEach(System.out::println);
        System.out.println();
        carService.getAllByDriver(1L).forEach(System.out::println);
    }
}
