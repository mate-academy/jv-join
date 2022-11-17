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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer bmwGermany = new Manufacturer("BMW", "Germany");
        Manufacturer skodaCzech = new Manufacturer("Skoda", "Czech");
        manufacturerService.create(bmwGermany);
        manufacturerService.create(skodaCzech);
        manufacturerService.getAll().forEach(System.out::println);
        bmwGermany.setCountry("Ukraine");
        manufacturerService.update(bmwGermany);
        System.out.println("-=-=-=-");
        System.out.println(manufacturerService.get(bmwGermany.getId()));
        System.out.println(manufacturerService.delete(bmwGermany.getId()));

        System.out.println("-=-=-=-");

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver bobBobenko = new Driver("Bob", "a-0001");
        Driver aliceAlicenko = new Driver("Alice", "a-0002");
        Driver jackJackovych = new Driver("Jack", "a-0003");
        Driver petroMostavchuk = new Driver("Petro", "a-0004");
        Driver harryPotter = new Driver("Harry", "a-0005");
        driverService.create(bobBobenko);
        driverService.create(aliceAlicenko);
        driverService.create(jackJackovych);
        driverService.create(petroMostavchuk);
        driverService.create(harryPotter);
        driverService.getAll().forEach(System.out::println);
        bobBobenko.setName("Bob Bobenko");
        driverService.update(bobBobenko);
        System.out.println("-=-=-=-");
        System.out.println(driverService.get(bobBobenko.getId()));
        System.out.println(driverService.delete(aliceAlicenko.getId()));
        System.out.println("-=-=-=-");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car skodaOctavia = new Car("Skoda Octavia", skodaCzech, List.of(
                bobBobenko, aliceAlicenko));
        Car bmwX5 = new Car("BMW X5", bmwGermany, List.of(
                jackJackovych, petroMostavchuk, harryPotter));
        carService.create(skodaOctavia);
        carService.create(bmwX5);
        carService.removeDriverFromCar(bobBobenko, skodaOctavia);
        carService.addDriverToCar(bobBobenko, bmwX5);
        bmwX5.setModel("BMW X5 Black");
        carService.update(bmwX5);
        System.out.println(carService.get(skodaOctavia.getId()));
        System.out.println(carService.getAllByDriver(bobBobenko.getId()));

    }
}
