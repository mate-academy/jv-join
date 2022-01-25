package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

import java.util.Optional;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {

        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Car car = Car.builder()
                .model("Tazik6")
                .manufacturer(manufacturerService.get(4L))
                .build();

        CarService carService = (CarService) injector.getInstance(CarService.class);
        //carService.create(car);

        Car car1 = carService.get(1L);
        System.out.println(car1);
        //System.out.println(carService.delete(car1.getId()));
        System.out.println("................");
        //carService.getAll().forEach(System.out::println);

        Driver driver = new Driver();


    }
}
