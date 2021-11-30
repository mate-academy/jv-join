package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        // Manufacturer manufacturer = new Manufacturer("iwtb", "Ukraine");
        // manufacturerService.create(manufacturer);
        // Car car = new Car("audi", Collections.emptyList(), manufacturer);
        // carService.create(car);
        System.out.println(carService.get(1L));
    }
}
