package mate.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverJohn = new Driver(1L, "John", "123 52r 12");
        Driver driverNatalie = new Driver(2L, "Natalie", "152 d34 03");
        Driver driverMark = new Driver(3L, "Mark", "163 2f2 13");
        Driver driverAsad = new Driver(4L, "Asad", "123 d31 54");
        if (driverService.getAll().isEmpty()) {
            driverService.create(driverJohn);
            driverService.create(driverNatalie);
            driverService.create(driverMark);
            driverService.create(driverAsad);
            System.out.println("Inserted drivers to db");
        }

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer bmwGroup = new Manufacturer(1L, "BMW Group", "Germany");
        Manufacturer hondaMotor = new Manufacturer(2L, "Honda Motor", "Japan");
        if (manufacturerService.getAll().isEmpty()) {
            manufacturerService.create(bmwGroup);
            manufacturerService.create(hondaMotor);
            System.out.println("Inserted manufacturers to db");
        }

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car bmw = new Car();
        bmw.setManufacturer(bmwGroup);
        bmw.setModel("x5");
        bmw.setDrivers(List.of(driverJohn, driverNatalie));
        Car createdCar = carService.create(bmw);
        System.out.println("Inserted car to db: " + createdCar);
        System.out.println("All cars from db after inserting: ");
        carService.getAll().forEach(System.out::println);

        Long createdCarId = createdCar.getId();
        System.out.println("Car by id: " + createdCarId
                + " from db: " + carService.get(createdCarId));

        Car honda = new Car();
        honda.setId(createdCarId);
        honda.setManufacturer(hondaMotor);
        honda.setModel("CR-V");
        honda.setDrivers(new ArrayList<>(Arrays.asList(driverMark, driverAsad)));
        System.out.println("Car by id: " + createdCarId
                + " from db after update: " + carService.update(honda));
        System.out.println("All cars from db after updating a car: ");
        carService.getAll().forEach(System.out::println);

        System.out.println("Added driver: "
                + driverJohn + " to the car by id: " + createdCarId);
        carService.addDriverToCar(driverJohn, honda);
        System.out.println("Car by id: "
                + createdCarId + " after adding new driver: " + honda);

        System.out.println("Removed driver: "
                + driverMark + " from the car by id: " + createdCarId);
        carService.removeDriverFromCar(driverMark, honda);
        System.out.println("Car by id: " + createdCarId + " after removing driver: " + honda);

        System.out.println("All cars by driver: " + driverJohn);
        carService.getAllByDriver(driverJohn.getId()).forEach(System.out::println);

        System.out.println("Deleting car from db: " + carService.delete(createdCarId));
        System.out.println("All cars in db after deleting car");
        carService.getAll().forEach(System.out::println);
    }
}
