package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.get(2L));
        Car druchok = carService.get(2L);
        druchok.setModel("Druchok");
        carService.update(druchok);
        carService.getAll().forEach(System.out::println);
    }
}
