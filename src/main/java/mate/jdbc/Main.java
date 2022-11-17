package mate.jdbc;

import java.util.ArrayList;
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
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        System.out.println("Driver");
        Driver bob = new Driver("Bob", "12345");
        Driver alice = new Driver("Alice", "6789");
        driverService.getAll().forEach(System.out::println);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        drivers.add(alice);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwManufacturer = new Manufacturer("BMW", "Germany");
        Manufacturer toyotaManufacturer = new Manufacturer("Toyota", "Japan");

        System.out.println("Manufacturer");
        manufacturerService.getAll().forEach(System.out::println);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car x5 = new Car("X5", bmwManufacturer, drivers);
        Car camry = new Car("camry", toyotaManufacturer, drivers);
        carService.addDriverToCar(bob, x5);
        carService.addDriverToCar(alice, camry);
        System.out.println("CarService");
        carService.getAll().forEach(System.out::println);
        Car someoneCar = carService.get(2L);
        someoneCar.setModel("3");
        someoneCar.setDrivers(drivers);
        System.out.println(carService.update(someoneCar));
        System.out.println(carService.getAllByDriver(bob.getId()));

    }
}
