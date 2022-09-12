package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        // test your code here
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println("test CarService.get");
        System.out.println(carService.get(2L));
        System.out.println("test CarService.getAll");
        carService.getAll().forEach(System.out::println);
        System.out.println("test CarService.getAllByDriver");
        carService.getAllByDriver(1L).forEach(System.out::println);
        System.out.println("test CarService.addDriverToCar");
        Driver george = new Driver("George", "FD7645IU");
        carService.get(1L).getDrivers().forEach(System.out::println);
        System.out.println("=========");
        carService.addDriverToCar(george, carService.get(1L));
        carService.get(1L).getDrivers().forEach(System.out::println);
        System.out.println("test CarService.removeDriverFromCar");
        carService.get(1L).getDrivers().forEach(System.out::println);
        System.out.println("=========");
        Driver george1 = new Driver(7L,"George", "FD7645IU");
        carService.removeDriverFromCar(george1, carService.get(1L));
        carService.get(1L).getDrivers().forEach(System.out::println);

    }
}
