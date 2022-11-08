package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("Dodge");
        carService.create(car);

        System.out.println();

        carService.getAll().forEach(c -> System.out.println(c.getModel()));
        System.out.println(carService.get(1L).get().getManufacturer().getName());
    }
}
