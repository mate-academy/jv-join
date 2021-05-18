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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer ferrari = new Manufacturer("Ferrari", "Italy");
        Manufacturer porsche = new Manufacturer("Porsche", "Germany");
        Manufacturer ford = new Manufacturer("Ford", "USA");

        manufacturerService.create(ferrari);
        manufacturerService.create(porsche);
        manufacturerService.create(ford);

        Driver bob = new Driver("Bob", "336644");
        Driver nick = new Driver("Nick", "349673");
        Driver susan = new Driver("Susan", "476738");

        driverService.create(bob);
        driverService.create(nick);
        driverService.create(susan);

        Car gto = new Car();
        gto.setModel("250 GTO");
        gto.setManufacturer(ferrari);
        Car carrera = new Car();
        carrera.setModel("Carrera");
        carrera.setManufacturer(porsche);
        Car mustang = new Car();
        mustang.setModel("Mustang");
        mustang.setManufacturer(ford);

        carService.create(gto);
        carService.create(carrera);
        carService.create(mustang);

        gto.setDrivers(new ArrayList<>(List.of(bob, susan)));
        carrera.setDrivers(new ArrayList<>(List.of(nick, susan)));
        mustang.setDrivers(new ArrayList<>(List.of(bob, nick)));
        carService.update(gto);
        carService.update(carrera);
        carService.update(mustang);

        System.out.println("Initial list of all cars:");
        List<Car> allCars = carService.getAll();
        allCars.forEach(System.out::println);

        System.out.println("Change Carrera to Carrera GT:");
        carrera.setModel("Carrera GT");
        System.out.println(carService.update(carrera));

        System.out.println("Changed Bob to Mighty Bob:");
        bob.setName("Mighty Bob");
        Driver newBob = driverService.update(bob);

        carService.removeDriverFromCar(bob, gto);
        carService.removeDriverFromCar(nick,carrera);
        carService.addDriverToCar(newBob, gto);

        System.out.println("List of all cars after change:");
        List<Car> allCarsAfterChange = carService.getAll();
        allCarsAfterChange.forEach(System.out::println);

        System.out.println("List of all cars Susan drives:");
        List<Car> allByDrivers = carService.getAllByDriver(susan.getId());
        allByDrivers.forEach(System.out::println);

        System.out.println("Delete some drivers and cars:");
        System.out.println(driverService.delete(nick.getId()));
        System.out.println(carService.delete(mustang.getId()));
        System.out.println(manufacturerService.delete(ford.getId()));

        System.out.println("List of all cars after delete:");
        List<Car> allCarsAfterDelete = carService.getAll();
        allCarsAfterDelete.forEach(System.out::println);
    }
}
