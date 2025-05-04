package mate.jdbc;

import java.util.ArrayList;
import java.util.Collections;
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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Car testCar = new Car();
        testCar.setId(6L);
        testCar.setModel("Audi2");
        testCar.setManufacturer(manufacturerService.get(3L));
        testCar.setDrivers(Collections.emptyList());
        CarService carService = (CarService) injector.getInstance(CarService.class);

        System.out.println(carService.create(testCar));
        System.out.println(carService.get(2L));
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.update(testCar));
        System.out.println(carService.delete(6L));
        System.out.println(carService.getAllByDriver(2L));

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driver1 = new Driver(1L, "Nelia", "U499");
        Driver driver2 = new Driver(2L, "Ihor", "QW000");
        Driver driver3 = new Driver(3L, "Alex", "GH783");
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);
        drivers.add(driver3);
        testCar.setDrivers(drivers);

        List<Car> allCars = carService.getAll();
        for (Car car : allCars) {
            System.out.println("before" + System.lineSeparator() + car);
        }
        carService.addDriverToCar(driverService.get(2L), testCar);
        carService.removeDriverFromCar(driver3, testCar);
        allCars = carService.getAll();
        for (Car car : allCars) {
            System.out.println("after" + System.lineSeparator() + car);
        }
    }
}
