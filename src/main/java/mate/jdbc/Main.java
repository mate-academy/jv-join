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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("seat");
        manufacturer.setCountry("spain");
        ManufacturerService manufacturerService = (ManufacturerService)injector
                .getInstance(ManufacturerService.class);
        final Manufacturer savedManufacturer = manufacturerService.create(manufacturer);

        Driver driverJerry = new Driver();
        driverJerry.setName("Jerry");
        driverJerry.setLicenseNumber("RA5678");
        DriverService driverService = (DriverService)injector.getInstance(DriverService.class);
        final Driver savedDriver = driverService.create(driverJerry);

        Car car = new Car();
        car.setModel("ibiza");
        car.setManufacturer(savedManufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(savedDriver);
        car.setDrivers(drivers);

        CarService carService = (CarService)injector.getInstance(CarService.class);

        Car savedCar = carService.create(car);

        System.out.println(carService.get(savedCar.getId()));

        carService.getAllByDriver(driverJerry.getId()).forEach(System.out::println);

        Driver driverAnna = new Driver();
        driverAnna.setName("Anna");
        driverAnna.setLicenseNumber("DR2368");
        Driver savedDriverAnna = driverService.create(driverAnna);
        carService.addDriverToCar(savedDriverAnna, savedCar);

        carService.getAll().forEach(System.out::println);

        carService.removeDriverFromCar(savedDriverAnna, savedCar);

        carService.getAll().forEach(System.out::println);

        carService.delete(savedCar.getId());

        carService.getAll().forEach(System.out::println);

    }
}
