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
        manufacturerService.create(manufacturerFord);
        manufacturerService.create(manufacturerBMW);
        //System.out.println(manufacturerService.get(manufacturerBMW.getId()));
        Car carX5 = new Car(manufacturerService.get(manufacturerBMW.getId()), "X5");
        Car carMustang = new Car(manufacturerService.get(manufacturerFord.getId()), "Mustang");
        carService.create(carX5);
        carService.create(carMustang);
        System.out.println(carService.get(carX5.getId()));
        carService.getAll().forEach(System.out::println);
    }
}
