package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bob = new Driver(1L, "Bob", "AE-76359853");
        Driver alice = new Driver(2L, "Alice", "AE-34764643");
        driverService.create(bob);
        driverService.create(alice);

        Manufacturer audi = new Manufacturer(1L, "Audi", "Germany");
        List<Driver> driversList = new ArrayList<>();
        driversList.add(bob);
        driversList.add(alice);
        Car testCarA4 = new Car(2L, "A4", audi);
        Car testCarA6 = new Car(1L, "A6", audi);
        testCarA4.setDrivers(driversList);
        testCarA6.setDrivers(driversList);

        CarService testCarService = (CarService) injector.getInstance(CarService.class);
        testCarService.create(testCarA4);
        testCarService.create(testCarA6);
        System.out.println(testCarService.getAll());
        System.out.println(testCarService.getAllByDriver(3L));
        testCarService.removeDriverFromCar(alice, testCarA4);
        System.out.println(testCarService.getAll());
    }
}
