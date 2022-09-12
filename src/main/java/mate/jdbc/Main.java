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
        Driver driverMaks = new Driver(1L, "Maks", "123 45q 78");
        Driver driverMasha = new Driver(2L, "Masha", "234 56w 78");
        Driver driverNick = new Driver(3L, "Nick", "345 67e 98");
        Driver driverJulia = new Driver(4L, "Julia", "456 78r 99");
        if (driverService.getAll().isEmpty()) {
            driverService.create(driverMaks);
            driverService.create(driverMasha);
            driverService.create(driverNick);
            driverService.create(driverJulia);
            System.out.println("Inserted drivers to db");
        }

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer jeepManufacturer = new Manufacturer(1L, "Jeep", "USA");
        Manufacturer nissanManufacturer = new Manufacturer(2L, "Nissan", "Japan");
        if (manufacturerService.getAll().isEmpty()) {
            manufacturerService.create(jeepManufacturer);
            manufacturerService.create(nissanManufacturer);
            System.out.println("Inserted manufacturers to db");
        }

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car jeep = new Car();
        jeep.setManufacturer(jeepManufacturer);
        jeep.setModel("COMPASS");
        jeep.setDrivers(List.of(driverMaks, driverMasha));
        Car createdCar = carService.create(jeep);
        System.out.println("Inserted car to db: " + createdCar);
        System.out.println("All cars from db after inserting: ");
        carService.getAll().forEach(System.out::println);

        Long createdCarId = createdCar.getId();
        System.out.println("Car by id: " + createdCarId
                + " from db: " + carService.get(createdCarId));

        Car nissan = new Car();
        nissan.setId(createdCarId);
        nissan.setManufacturer(nissanManufacturer);
        nissan.setModel("Leaf");
        nissan.setDrivers(new ArrayList<>(Arrays.asList(driverNick, driverJulia)));
        System.out.println("Car by id: " + createdCarId
                + " from db after update: " + carService.update(nissan));
        System.out.println("All cars from db after updating a car: ");
        carService.getAll().forEach(System.out::println);

        System.out.println("Added driver: "
                + driverMaks + " to the car by id: " + createdCarId);
        carService.addDriverToCar(driverMaks, nissan);
        System.out.println("Car by id: "
                + createdCarId + " after adding new driver: " + nissan);

        System.out.println("Removed driver: "
                + driverNick + " from the car by id: " + createdCarId);
        carService.removeDriverFromCar(driverNick, nissan);
        System.out.println("Car by id: " + createdCarId + " after removing driver: " + nissan);

        System.out.println("All cars by driver: " + driverMaks);
        carService.getAllByDriver(driverMaks.getId()).forEach(System.out::println);

        System.out.println("Deleting car from db: " + carService.delete(createdCarId));
        System.out.println("All cars in db after deleting car");
        carService.getAll().forEach(System.out::println);
    }
}
