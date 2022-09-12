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
        Driver firstDriver = new Driver();
        firstDriver.setName("Steve");
        firstDriver.setLicenseNumber("Steve#1111");
        Driver secondDriver = new Driver();
        secondDriver.setName("Elon");
        secondDriver.setLicenseNumber("Elon#3232");
        Driver thirdDriver = new Driver();
        thirdDriver.setName("Kate");
        thirdDriver.setLicenseNumber("Kate#7982");
        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);

        System.out.println("___________Create a few drivers___________");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        System.out.println(driverService.create(firstDriver));
        System.out.println(driverService.create(secondDriver));
        System.out.println(driverService.create(thirdDriver));

        System.out.println("___________Create a manufacturer___________");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Tesla");
        manufacturer.setCountry("USA");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        System.out.println(manufacturerService.create(manufacturer));

        System.out.println("___________Create a car___________");
        Car car = new Car();
        car.setManufacturer(manufacturer);
        car.setModel("X");
        driverService.get(firstDriver.getId());
        driverService.get(secondDriver.getId());
        car.setDrivers(drivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(car));

        System.out.println("___________Get all cars by driver id___________");
        carService.getAllByDriver(firstDriver.getId()).forEach(System.out::println);

        System.out.println("___________Update a car___________");
        car.setModel("S");
        System.out.println(carService.update(car));

        System.out.println("___________Get a car by id after adding a new driver___________");

        carService.addDriverToCar(thirdDriver, car);
        System.out.println(carService.get(car.getId()));

        System.out.println("___________Get a car by id after remove a driver___________");
        carService.removeDriverFromCar(firstDriver, car);
        System.out.println(carService.get(car.getId()));

        System.out.println("___________Delete a car___________");
        System.out.println(carService.delete(car.getId()));

        System.out.println("___________Get all cars___________");
        carService.getAll().forEach(System.out::println);
    }
}
