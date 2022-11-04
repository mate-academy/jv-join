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
        Manufacturer volvo = new Manufacturer(1L, "volvo", "Sweden");
        Manufacturer bmw = new Manufacturer(2L, "bmw", "germany");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        System.out.println(manufacturerService.create(volvo));
        System.out.println(manufacturerService.create(bmw));

        Driver bob = new Driver();
        bob.setLicenseNumber("12345gfhhghgf");
        bob.setName("Bob");
        bob.setId(11L);
        Driver roma = new Driver();
        roma.setLicenseNumber("gagagsdgsadgasdg");
        roma.setName("Roma");
        roma.setId(14L);
        Driver alice = new Driver();
        alice.setName("Alice");
        alice.setLicenseNumber("6789gfhgfd");
        alice.setId(12L);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(bob);
        driverService.create(alice);
        driverService.create(roma);

        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(roma);
        Car xc90 = new Car();
        xc90.setModel("XC90");
        xc90.setManufacturer(volvo);
        Car m4 = new Car();
        m4.setManufacturer(bmw);
        m4.setModel("M4");
        m4.setDriverList(drivers1);
        Car updatedCar = new Car();
        updatedCar.setModel("M8");
        updatedCar.setManufacturer(bmw);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(alice);
        drivers.add(bob);
        updatedCar.setDriverList(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println("Creating cars....");
        System.out.println(carService.create(m4));
        System.out.println(carService.create(xc90));
        System.out.println("Cars successfully created!!!" + System.lineSeparator()
                + "----------------------------------");

        System.out.println("Getting car with id = " + m4.getId() + " ..........");
        System.out.println(carService.get(m4.getId()));
        System.out.println("Car successfully getted");
        System.out.println("----------------------------------");

        System.out.println("Getting all cars........");
        carService.getAll().forEach(System.out::println);
        System.out.println("All cars successfully getted");
        System.out.println("----------------------------------");

        System.out.println("Adding driver to car.....");
        carService.addDriverToCar(bob, m4);
        carService.addDriverToCar(alice, m4);
        System.out.println("Driver successfully added");
        System.out.println("----------------------------------");

        System.out.println("Updating car.....");
        System.out.println(m4.getId());
        updatedCar.setId(m4.getId());
        System.out.println(carService.update(updatedCar));
        System.out.println("Car successfully updated");
        System.out.println("----------------------------------");

        System.out.println("Deleting car.....");
        System.out.println(carService.delete(xc90.getId()));
        System.out.println("Car successfully deleted");
        System.out.println("----------------------------------");

        System.out.println("Removing driver from car....");
        carService.removeDriverFromCar(bob, m4);
        System.out.println(carService.get(m4.getId()));
        System.out.println("Driver successfully deleted");
        System.out.println("----------------------------------");

        System.out.println("Getting all by driver");
        System.out.println(carService.getAllByDriver(alice.getId()));
        System.out.println("All successfully getted");
        System.out.println("----------------------------------");
    }
}
