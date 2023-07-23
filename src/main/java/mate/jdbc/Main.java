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

        Driver bob = driverService.get(5L);
        Driver john = driverService.get(6L);
        Driver alice = driverService.get(7L);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(bob);
        drivers.add(john);
        drivers.add(alice);

        Driver ivan = new Driver("Ivan", "777");
        driverService.create(ivan);

        Manufacturer rollsRoyce = manufacturerService.get(21L);
        Car ghost = new Car("Ghost", rollsRoyce, drivers);
        carService.create(ghost);
        System.out.println("Create test _____\n" + carService.get(ghost.getId()));
        System.out.println("Get all test_____");
        carService.getAll().forEach(System.out::println);

        ghost.setModel("412");
        System.out.println("Update test_____\n" + carService.update(ghost));

        carService.addDriverToCar(ivan, ghost);
        System.out.println("Add driver to car test ____\n" + carService.get(ghost.getId()));

        carService.removeDriverFromCar(bob, ghost);
        System.out.println("Remove driver from car test____\n" + carService.get(ghost.getId()));

        System.out.println("Get all by driver test____");
        carService.getAllByDriver(alice.getId()).forEach(System.out::println);

        System.out.println(carService.delete(ghost.getId()));
    }
}
