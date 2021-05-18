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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturerAudi = new Manufacturer("audi", "Germany");
        Manufacturer manufacturerBwm = new Manufacturer("bwm", "Germany");

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturerAudi);
        manufacturerService.create(manufacturerBwm);

        Driver driverBob = new Driver("Bob", "N1234");
        Driver driverMax = new Driver("Max", "N127689");
        Driver driverSam = new Driver("Sam", "N19121");

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverBob);
        driverService.create(driverMax);
        driverService.create(driverSam);

        List<Driver> vipCategoryDrivers = new ArrayList<>();
        vipCategoryDrivers.add(driverMax);
        vipCategoryDrivers.add(driverSam);
        List<Driver> firstCategoryDrivers = driverService.getAll();

        Car carA4 = new Car();
        carA4.setModel("a4");
        carA4.setManufacturer(manufacturerAudi);
        carA4.setDrivers(firstCategoryDrivers);

        Car carQ8 = new Car();
        carQ8.setModel("q8");
        carQ8.setManufacturer(manufacturerAudi);
        carQ8.setDrivers(vipCategoryDrivers);

        Car carX5 = new Car();
        carX5.setModel("x5");
        carX5.setManufacturer(manufacturerBwm);
        carX5.setDrivers(vipCategoryDrivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(carA4);
        carService.create(carQ8);
        carService.create(carX5);

        System.out.println("--------------------List of cars after calling method create(): ");
        carService.getAll().forEach(System.out::println);

        System.out.println("--------------------Car before update(): ");
        System.out.println(carService.get(carX5.getId()));
        carX5.setModel("x6");
        System.out.println("--------------------Car after update(): ");
        System.out.println(carService.update(carX5));

        System.out.println("--------------------Method delete() was executed successfully: ");
        System.out.println(carService.delete(carA4.getId()));

        System.out.println("--------------------Car before add() driver: ");
        System.out.println(carService.get(carQ8.getId()));
        carService.addDriverToCar(driverBob, carQ8);
        System.out.println("--------------------Car after add() driver: ");
        System.out.println(carService.get(carQ8.getId()));

        System.out.println("--------------------Car before remove() driver:");
        System.out.println(carService.get(carX5.getId()));
        carService.removeDriverFromCar(driverMax, carX5);
        System.out.println("--------------------Car after remove() driver: ");
        System.out.println(carService.get(carX5.getId()));

        System.out.println("--------------------List of cars after calling getAllByDriverId(): ");
        System.out.println(carService.getAllByDriver(driverBob.getId()));
    }
}
