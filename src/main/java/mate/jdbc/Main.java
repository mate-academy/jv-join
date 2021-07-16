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
    private static final Injector injector =
            Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");

        manufacturerService.create(bmw);

        Driver veronika = new Driver();
        veronika.setName("Veronika");
        veronika.setLicenseNumber("1234");

        Driver maksim = new Driver();
        maksim.setName("Maksim");
        maksim.setLicenseNumber("1234");

        System.out.println("Add driver to db...");
        driverService.create(veronika);

        System.out.println("Add driver to db...");
        driverService.create(maksim);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(veronika);
        System.out.println("Drivers was added...");

        Car bmwX3 = new Car();
        bmwX3.setModel("X3");
        bmwX3.setManufacturer(bmw);
        bmwX3.setDrivers(drivers);

        Car bmwX5 = new Car();
        bmwX5.setModel("X5");
        bmwX5.setManufacturer(bmw);
        bmwX5.setDrivers(drivers);

        System.out.println("Add car to db...");
        carService.create(bmwX3);

        System.out.println("Add car to db...");
        carService.create(bmwX5);

        System.out.println("Get car from db...");
        System.out.println(carService.get(bmwX3.getId()));

        System.out.println("Get cars by driver...");
        System.out.println(carService.getAllByDriver(veronika.getId()));

        System.out.println("Get all cars...");
        carService.getAll().forEach(System.out::println);

        System.out.println("Update car in db...");
        bmwX3.setModel("Old X3");
        carService.update(bmwX3);

        System.out.println("Delete car from db...");
        carService.delete(bmwX3.getId());

        System.out.println("Add driver to car...");
        carService.addDriverToCar(maksim, bmwX5);

        System.out.println("Remove driver from car...");
        carService.removeDriverFromCar(maksim, bmwX5);
    }
}
