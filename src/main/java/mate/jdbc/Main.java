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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        // test your code here
        Manufacturer reno = new Manufacturer("Lamborghini","Italy");
        Manufacturer toyota = new Manufacturer("Toyota","Japan");
        manufacturerService.create(reno);
        manufacturerService.create(toyota);
        List<Manufacturer> allManufacturers = manufacturerService.getAll();
        allManufacturers.forEach(System.out::println);
        System.out.println(manufacturerService.get(toyota.getId()));
        reno.setName("Mazda");
        reno.setCountry("China");
        System.out.println(manufacturerService.update(reno));
        System.out.println(manufacturerService.delete(toyota.getId()));

        Driver bob = new Driver("Alex","17825690");
        Driver john = new Driver("Tom", "58046245");
        Driver alice = new Driver("Jane", "07357892");
        driverService.create(bob);
        driverService.create(john);
        driverService.create(alice);
        List<Driver> allDrivers = driverService.getAll();
        allDrivers.forEach(System.out::println);
        System.out.println(driverService.get(alice.getId()));
        john.setName("Rick");
        john.setLicenseNumber("98367845");
        System.out.println(driverService.update(john));
        System.out.println(driverService.delete(bob.getId()));

        Car volvo = new Car();
        volvo.setModel("Volvo");
        volvo.setManufacturer(reno);
        List<Driver> driverListForVolvo = new ArrayList<>();
        driverListForVolvo.add(alice);
        driverListForVolvo.add(bob);
        volvo.setDrivers(driverListForVolvo);
        Car audi = new Car();
        audi.setModel("Audi");
        audi.setManufacturer(toyota);
        List<Driver> driverListForAudi = new ArrayList<>();
        driverListForAudi.add(john);
        driverListForAudi.add(bob);
        audi.setDrivers(driverListForAudi);
        System.out.println(carService.create(volvo));
        System.out.println(carService.create(audi));
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.get(volvo.getId()));
        volvo.setModel("volvoX40");
        System.out.println(carService.update(volvo));
        System.out.println(carService.delete(audi.getId()));
        carService.addDriverToCar(john, volvo);
        carService.removeDriverFromCar(bob,audi);
        carService.getAllByDriver(bob.getId()).forEach(System.out::println);
    }
}
