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
    private static final int INDEX_CAR_FOR_UPDATE = 0;
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "11111");
        Driver alice = new Driver("Alice", "22222");
        Driver kate = new Driver("Kate", "33333");
        List<Driver> drivers = List.of(bob, alice, kate);
        drivers.stream()
                .map(driverService::create)
                .forEach(System.out::println);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer ford = new Manufacturer("Ford", "USA");
        Manufacturer fiat = new Manufacturer("Fiat", "Italy");
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer nissan = new Manufacturer("Nissan", "Japan");
        List<Manufacturer> manufacturers = List.of(ford, fiat, audi, nissan);
        manufacturers.stream()
                .map(manufacturerService::create)
                .forEach(System.out::println);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.getAll().stream()
                .forEach(car -> carService.delete(car.getId()));
        List<Car> cars = List.of(new Car("A6", audi, List.of(bob, alice)),
                new Car("Kuga", ford, List.of(bob, kate)),
                new Car("Juke", nissan, List.of(alice)));
        cars.stream()
                .map(carService::create)
                .forEach(System.out::println);
        Car carForUpdate = cars.get(INDEX_CAR_FOR_UPDATE);
        System.out.println(carForUpdate);
        carService.addDriverToCar(kate, carForUpdate);
        carService.removeDriverFromCar(bob, carForUpdate);
        System.out.println(carService.get(carForUpdate.getId()));
        carForUpdate.setModel("Q5");
        System.out.println(carService.update(carForUpdate));
        System.out.println(carService.get(cars.get(INDEX_CAR_FOR_UPDATE).getId()));
        System.out.println("getAllByDriver");
        carService.getAllByDriver(kate.getId()).stream()
                .forEach(System.out::println);
        System.out.println("delete");
        System.out.println(carService.delete(cars.get(1).getId()));
        System.out.println("getALL");
        carService.getAll().stream()
                .forEach(System.out::println);
    }
}
