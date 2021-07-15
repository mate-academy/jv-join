package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Driver andrew = new Driver("Andrew", "01");
        Driver franco = new Driver("Franco", "11");
        Manufacturer japan = new Manufacturer("Nissan", "Japan");
        Car maxima = new Car("Maxima", japan, List.of(andrew, franco));
        System.out.println(carService.create(maxima));
        System.out.println(carService.get(maxima.getId()));
        carService.getAll().forEach(System.out::println);
        maxima.setModel("Mikra");
        System.out.println(carService.update(maxima));
        System.out.println("**********Before Delete************");
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(13L));
        System.out.println("**********After Delete************");
        carService.getAll().forEach(System.out::println);
        Car eight = carService.get(8L);
        System.out.println("get car:" + eight);
        Driver benito = new Driver("Benito", "91");
        carService.addDriverToCar(benito, eight);
        System.out.println("after adding " + carService.get(8L));
        carService.removeDriverFromCar(benito, eight);
        System.out.println("after removing " + carService.get(8L));
        System.out.println("**********Get all by driver************");
        System.out.println(carService.getAllByDriver(2L));
    }
}
