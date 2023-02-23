package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;

public class Main {
    public static void main(String[] args) {
        Injector injector = Injector.getInstance("mate.jdbc");
        CarService carService = (CarService) injector.getInstance(CarService.class);

        //1. test carService.get();
        System.out.println(carService.get(2L));

        //2. test carService.getAllByDriver()
        System.out.println(carService.getAllByDriver(2L));

        //3. test carService.getAll()
        System.out.println(carService.getAll());
    }
}
