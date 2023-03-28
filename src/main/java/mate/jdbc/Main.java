package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.DriverServiceImpl;
import mate.jdbc.service.ManufacturerService;
import mate.jdbc.service.ManufacturerServiceImpl;

public class Main {
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static DriverService driverService =
            (DriverServiceImpl) injector.getInstance(DriverService.class);
    private static ManufacturerService manufacturerService =
            (ManufacturerServiceImpl) injector.getInstance(ManufacturerService.class);
    private static CarService carService = (CarServiceImpl) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Driver alice = new Driver("Alice", "CATTYMOM2012");
        Driver bob = new Driver("Bob", "P1U33SS7Y");
        Driver jack = new Driver("Jack", "HUNTER822");
        Driver john = new Driver("John", "UBIWASHKA1337");
        Driver iryna = new Driver("Iryna", "OCEANSIREN2906");
        Driver maksym = new Driver("Maksym", "NIMAKEL2906");

        driverService.create(alice);
        driverService.create(bob);
        driverService.create(jack);
        driverService.create(john);
        driverService.create(iryna);
        driverService.create(maksym);

        final List<Driver> firstDriverList = new ArrayList<>();
        final List<Driver> secondDriverList = new ArrayList<>();
        final List<Driver> thirdDriverList = new ArrayList<>();

        firstDriverList.add(alice);
        firstDriverList.add(bob);
        secondDriverList.add(jack);
        secondDriverList.add(john);
        thirdDriverList.add(iryna);
        thirdDriverList.add(maksym);

        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer lada = new Manufacturer("Lada", "Soviet Union");
        Manufacturer ford = new Manufacturer("Ford", "USA");

        manufacturerService.create(toyota);
        manufacturerService.create(lada);
        manufacturerService.create(ford);

        Car carToyota = new Car("Camry", toyota, thirdDriverList);
        Car carLada = new Car("Zhiga", lada, secondDriverList);
        Car carFord = new Car("Evanda", ford, firstDriverList);

        carService.create(carToyota);
        carService.create(carLada);
        carService.create(carFord);

        System.out.println("Get all cars: \n" + carService.getAll());

        carService.removeDriverFromCar(john, carLada);
        carService.addDriverToCar(john, carFord);

        System.out.println("Get all cars after removing John: \n" + carService.getAll());

        carLada.setModel("Zhigul");
        carService.update(carLada);
        System.out.println("Get Lada car after changing model: \n"
                + carService.get(carLada.getId()));

        System.out.println("Get all by Maksym ID: \n"
                + carService.getAllByDriver(thirdDriverList.get(1).getId()));

        carService.delete(carLada.getId());
        System.out.println("Get all after deleting Lada car: \n"
                + carService.getAll());

        System.out.println("Get car by ID: \n" + carService.get(carToyota.getId()));
    }
}
