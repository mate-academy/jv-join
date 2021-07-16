package mate.jdbc;

import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Driver andrew = driverService.get(16L);
        Driver franco = driverService.get(17L);
        Manufacturer japan = manufacturerService.get(1L);
        Car maxima = new Car("Maxima", japan, List.of(andrew, franco));
        System.out.println(carService.create(maxima));
        System.out.println(carService.get(maxima.getId()));
        carService.getAll().forEach(System.out::println);
        maxima.setModel("Mikra");
        System.out.println(carService.update(maxima));
        System.out.println("**********Before Delete************");
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.delete(12L));
        System.out.println("**********After Delete************");
        carService.getAll().forEach(System.out::println);
        Car eight = carService.get(8L);
        System.out.println("get car:" + eight);
        Driver benito = driverService.get(7L);
        carService.addDriverToCar(benito, eight);
        System.out.println("after adding " + carService.get(8L));
        carService.removeDriverFromCar(benito, eight);
        System.out.println("after removing " + carService.get(8L));
        System.out.println("**********Get all by driver************");
        System.out.println(carService.getAllByDriver(2L));
    }
}
