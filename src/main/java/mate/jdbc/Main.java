package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carOne = carService.get(1L);
    }
}
