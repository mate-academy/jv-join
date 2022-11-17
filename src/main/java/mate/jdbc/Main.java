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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturerDaewoo = new Manufacturer("Daewoo", "South Korea");
        Manufacturer manufacturerNissan = new Manufacturer("Nissan", "Japan");
        System.out.println(List.of(manufacturerDaewoo, manufacturerNissan));
        manufacturerService.create(manufacturerDaewoo);
        manufacturerService.create(manufacturerNissan);

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driverJohn = new Driver("John", "12345678");
        Driver driverBob = new Driver("Bob", "87654321");
        Driver driverDen = new Driver("Den", "15935782");
        Driver driverSam = new Driver("Sam", "45685219");
        driverService.create(driverJohn);
        driverService.create(driverBob);
        driverService.create(driverDen);
        driverService.create(driverSam);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carDaewoo = new Car("Lanos", manufacturerDaewoo,
                List.of(driverJohn, driverBob));
        Car carNissan = new Car("Juke", manufacturerNissan,
                List.of(driverDen, driverSam));
        carService.create(carDaewoo);
        carService.create(carNissan);

        System.out.println("Get Daewoo Lanos:");
        System.out.println(carService.get(carDaewoo.getId()));
        System.out.println("Get all cars:");
        carService.getAll().forEach(System.out::println);
        System.out.println("Updated car:");
        carNissan.setModel("Quashqai");
        System.out.println(carService.update(carNissan));
        System.out.println("Get all by driver:");
        carService.getAllByDriver(driverJohn.getId()).forEach(System.out::println);
        carService.addDriverToCar(driverJohn, carNissan);
        carService.removeDriverFromCar(driverBob, carDaewoo);
        if (carService.delete(carNissan.getId())) {
            System.out.println("Car Nissan was deleted");
        }
    }
}
