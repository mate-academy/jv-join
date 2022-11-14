package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        CarService carService = (CarService) injector.getInstance(CarService.class);
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver firstDriver = driverService.get(3L); // Name: Oleg. LicenseNumber: 7895687
        Driver secondDriver = driverService.get(9L); // Name: Yarema. LicenseNumber: 7903502

        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);

        runTests(carService, driverService, manufacturerService, drivers);
    }

    private static void runTests(CarService carService, DriverService driverService,
                                 ManufacturerService manufacturerService,
                                 List<Driver> drivers) {

        System.out.println("Get 'X210' model car");
        System.out.println(carService.get(1L));

        System.out.println("Delete 'X210' model car");
        carService.delete(1L);

        System.out.println("Create 'MD300' model car");
        carService.create(new Car("MD300", manufacturerService.get(1L), drivers));

        System.out.println("Updating the car with a new driver");
        drivers.add(new Driver(3L, "Kakashi", "0123524"));
        carService.update(new Car(1L, "X220", manufacturerService.get(1L), drivers));

        System.out.println("Get all cars");
        for (Car car : carService.getAll()) {
            System.out.println(car);
        }

        System.out.println("Adding new driver to the car");
        carService.addDriverToCar(driverService.get(3L), carService.get(2L));

        System.out.println("Removing the driver from the car");
        carService.removeDriverFromCar(driverService.get(3L), carService.get(2L));
        System.out.println(carService.get(2L));

        System.out.println("Get all cars by driver");
        for (Car car : carService.getAllByDriver(driverService.get(3L).getId())) {
            System.out.println(car);
        }
    }
}
