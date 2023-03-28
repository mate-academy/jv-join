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
        // ManufacturerService methods test
        Manufacturer bmw = new Manufacturer();
        bmw.setName("BMW");
        bmw.setCountry("Germany");
        Manufacturer chevrolet = new Manufacturer();
        chevrolet.setName("Fiat");
        chevrolet.setCountry("Italia");
        System.out.println(manufacturerService.create(bmw));
        System.out.println(manufacturerService.create(chevrolet));
        Manufacturer forDelete = new Manufacturer();
        chevrolet.setName("Unknown");
        chevrolet.setCountry("Unknown");
        manufacturerService.create(forDelete);

        chevrolet.setName("Chevrolet");
        chevrolet.setCountry("USA");
        System.out.println(manufacturerService.update(chevrolet));

        manufacturerService.delete(forDelete.getId());
        System.out.println(manufacturerService.getAll());

        // DriverService methods test
        Driver mike = new Driver();
        mike.setName("Mike");
        mike.setLicenseNumber("92837465");
        driverService.create(mike);

        Driver marcus = new Driver();
        marcus.setName("Mario");
        marcus.setLicenseNumber("12321290");
        driverService.create(marcus);

        Driver alex = new Driver();
        alex.setName("Alex");
        alex.setLicenseNumber("32323243");
        driverService.create(alex);

        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);

        System.out.println(driverService.get(mike.getId()));

        mike.setName("Marcus");
        mike.setLicenseNumber("11111111");
        System.out.println(driverService.update(mike));

        driverService.delete(mike.getId());
        System.out.println(driverService.getAll());

        // CarService methods test
        Car car = new Car();
        car.setModel("X4");
        car.setManufacturer(bmw);
        car.setDrivers(new ArrayList<>());
        car.getDrivers().add(mike);
        car.getDrivers().add(marcus);
        carService.create(car);

        Car secondCar = new Car();
        secondCar.setModel("Camaro");
        secondCar.setManufacturer(chevrolet);
        secondCar.setDrivers(new ArrayList<>());
        secondCar.getDrivers().add(alex);
        carService.create(secondCar);
        System.out.println(carService.get(secondCar.getId()));

        carService.getAll().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(alex.getId()));

        carService.removeDriverFromCar(marcus, car);
        System.out.println(carService.get(car.getId()));

        carService.addDriverToCar(marcus, secondCar);
        System.out.println(carService.get(secondCar.getId()));

        car.setModel("X6");
        System.out.println(carService.update(car));

        carService.delete(car.getId());
        System.out.println(carService.getAll());
    }
}
