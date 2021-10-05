package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerBMW = new Manufacturer("BMW", "Germany");
        Manufacturer manufacturerFord = new Manufacturer("Ford", "USA");
        Manufacturer manufacturerPorsche = new Manufacturer("Porsche", "Germany");
        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerBMW);
        manufacturerService.create(manufacturerPorsche);
        Car carX5 = new Car(manufacturerBMW, "X5");
        Car carMustang = new Car(manufacturerFord, "Mustang");
        Car carCayenne = new Car(manufacturerPorsche, "Cayenne");
        carService.create(carX5);
        carService.create(carMustang);
        carService.getAll().forEach(System.out::println);
        carCayenne.setId(carX5.getId());
        carService.update(carCayenne);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(carCayenne.getId()));
        carService.delete(carMustang.getId());
        carService.getAll().forEach(System.out::println);
    }
}
