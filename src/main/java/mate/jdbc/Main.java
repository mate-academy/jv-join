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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver("Bob", "123qwe456rty");
        Driver alice = new Driver("Alice", "789uio123asd");
        Driver john = new Driver("John", "1234qwer5678");
        Driver mike = new Driver("Mike", "7890asdf6543");
        driverService.create(john);
        driverService.create(mike);
        driverService.create(bob);
        driverService.create(alice);
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        Manufacturer ferrariManufacturer = new Manufacturer("ferrari", "Italy");
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(ferrariManufacturer);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audi = new Car("Q7", audiManufacturer, List.of(bob, alice));
        Car ferrari = new Car("125S", ferrariManufacturer, List.of(john, mike));
        carService.create(audi);
        carService.create(ferrari);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(bob, audi);
        carService.removeDriverFromCar(mike, ferrari);
        carService.addDriverToCar(mike, audi);
        carService.addDriverToCar(bob, ferrari);
        System.out.println(carService.get(audi.getId()));
        System.out.println(carService.get(ferrari.getId()));
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
    }
}
