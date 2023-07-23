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
    private static final ManufacturerService manSer =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService drSer =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carSer =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {

        Driver bob = drSer.get(5L);
        Driver john = drSer.get(6L);
        Driver alice = drSer.get(7L);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        drivers.add(john);
        drivers.add(alice);

        Driver ivan = new Driver();
        ivan.setName("Ivan");
        ivan.setLicenseNumber("777");
        drSer.create(ivan);

        Manufacturer rollsRoyce = manSer.get(21L);
        Car ghost = new Car();
        ghost.setModel("Ghost");
        ghost.setManufacturer(rollsRoyce);
        ghost.setDrivers(drivers);

        carSer.create(ghost);
        System.out.println("Create test _____\n" + carSer.get(ghost.getId()));
        System.out.println("Get all test_____");
        carSer.getAll().forEach(System.out::println);

        ghost.setModel("412");
        System.out.println("Update test_____\n" + carSer.update(ghost));

        carSer.addDriverToCar(ivan, ghost);
        System.out.println("Add driver to car test ____\n" + carSer.get(ghost.getId()));

        carSer.removeDriverFromCar(bob, ghost);
        System.out.println("Remove driver from car test____\n" + carSer.get(ghost.getId()));

        System.out.println("Get all by driver test");
        carSer.getAllByDriver(alice.getId()).forEach(System.out::println);

        System.out.println(carSer.delete(ghost.getId()));
    }
}
