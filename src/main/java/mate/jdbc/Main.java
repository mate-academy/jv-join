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
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Opel");
        manufacturer.setCountry("Germany");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);

        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("Volkswagen");
        manufacturer1.setCountry("Germany");
        manufacturerService.create(manufacturer1);

        Driver bob = new Driver();
        bob.setName("Bob");
        bob.setLicenseNumber("123456");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(bob);

        Driver alice = new Driver();
        alice.setName("Alice");
        alice.setLicenseNumber("456789");
        driverService.create(alice);

        Driver john = new Driver();
        john.setName("John");
        john.setLicenseNumber("987654");
        driverService.create(john);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(driverService.get(27L));
        drivers.add(driverService.get(29L));

        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(driverService.get(28L));

        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("Vectra C");
        car.setAllDriverForCar(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        Car car1 = new Car();
        car1.setManufacturer(manufacturer1);
        car1.setModel("Golf");
        car1.setAllDriverForCar(drivers1);
        carService.create(car1);

        Driver bob1 = driverService.get(27L);
        Car car2 = carService.get(9L);
        carService.removeDriverFromCar(bob1, car2);

        carService.getAll();
        carService.getAllByDriver(28L);

        Driver mark = new Driver();
        mark.setName("Mark");
        mark.setLicenseNumber("654159");
        driverService.create(mark);

        Driver mark1 = driverService.get(30L);
        carService.addDriverToCar(mark1, car2);

        carService.delete(10L);
    }
}
