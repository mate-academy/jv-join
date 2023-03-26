package mate.jdbc;

import java.util.ArrayList;
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
        Manufacturer manufacturer1 = new Manufacturer(null, "BMW", "Germany");
        Manufacturer manufacturer2 = new Manufacturer(null, "Toyota", "Japan");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturer1 = manufacturerService.create(manufacturer1);
        System.out.println("Create : " + manufacturer1);
        manufacturer2 = manufacturerService.create(manufacturer2);
        System.out.println("Create : " + manufacturer2);

        Driver driver1 = new Driver(null, "Ivan", "AA7777XA");
        Driver driver2 = new Driver(null, "Petro", "BC1715CE");
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driver1 = driverService.create(driver1);
        System.out.println("Create : " + driver1);
        driver2 = driverService.create(driver2);
        System.out.println("Create : " + driver2);

        Car car1 = new Car(null, "320d", manufacturer1, new ArrayList<>());
        car1.getDrivers().add(driver1);
        car1.getDrivers().add(driver2);
        Car car2 = new Car(null, "Corolla", manufacturer2, new ArrayList<>());
        car2.getDrivers().add(driver1);
        Car car3 = new Car(null, "Auris", manufacturer2, new ArrayList<>());
        car3.getDrivers().add(driver2);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        car1 = carService.create(car1);
        System.out.println("Create : " + car1);
        car2 = carService.create(car2);
        System.out.println("Create : " + car2);
        car3 = carService.create(car3);
        System.out.println("Create : " + car3);
        System.out.println("Get first : " + carService.get(car1.getId()));
        System.out.println("Get all:");
        carService.getAll().forEach(System.out::println);
        car1.setModel("320i");
        System.out.println("Update : " + carService.update(car1));
        System.out.println("Delete success : " + carService.delete(car1.getId()));
        System.out.println("Table of cars after delete:");
        carService.getAll().forEach(System.out::println);
        System.out.println("Drivers before adding :" + car2.getDrivers());
        carService.addDriverToCar(driver2, car2);
        car2 = carService.get(car2.getId());
        System.out.println("Drivers after adding :" + car2.getDrivers());
        carService.removeDriverFromCar(driver1, car2);
        car2 = carService.get(car2.getId());
        System.out.println("Drivers after removing :" + car2.getDrivers());
        System.out.println("All cars with " + driver2 + " : ");
        carService.getAllByDriver(driver2.getId()).forEach(System.out::println);
    }
}
