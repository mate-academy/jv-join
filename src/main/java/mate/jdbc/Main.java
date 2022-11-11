package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Driver firstDriver = new Driver();
        firstDriver.setName("Jack");
        firstDriver.setLicenseNumber("AD1358_Jack");
        Driver secondDriver = new Driver();
        secondDriver.setName("Sally");
        secondDriver.setLicenseNumber("AD1321_Sally");
        Driver thirdDriver = new Driver();
        thirdDriver.setName("Petro");
        thirdDriver.setLicenseNumber("AD3455_Petro");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);

        System.out.println("Create a drivers...");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        System.out.println(driverService.create(firstDriver));
        System.out.println(driverService.create(secondDriver));
        System.out.println(driverService.create(thirdDriver));

        System.out.println("Create a manufacturer...");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Volkswagen");
        manufacturer.setCountry("Germany");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        System.out.println(manufacturerService.create(manufacturer));

        System.out.println("Create a car...");
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("ID3");
        driverService.get(firstDriver.getId());
        driverService.get(secondDriver.getId());
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        System.out.println("Get all cars by driver id...");
        carService.getAllByDriver(firstDriver.getId()).forEach(System.out::println);

        System.out.println("Update a car...");
        car.setModel("ID4");
        System.out.println(carService.update(car));

        System.out.println("Get a car by id after adding a new driver...");

        carService.addDriverToCar(thirdDriver, car);
        System.out.println(carService.get(car.getId()));

        System.out.println("Get a car by id after remove a driver...");
        carService.removeDriverFromCar(firstDriver, car);
        System.out.println(carService.get(car.getId()));

        System.out.println("Delete a car...");
        System.out.println(carService.delete(car.getId()));

        System.out.println("Get all cars...");
        carService.getAll().forEach(System.out::println);
    }
}
