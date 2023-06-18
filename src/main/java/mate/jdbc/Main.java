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
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setName("BMW");
        manufacturer.setCountry("Germany");

        Car car = new Car();
        car.setModel("X6");
        car.setManufacturer(manufacturer);

        Car createdCar = carService.create(car);
        System.out.println("Created car: " + createdCar);

        List<Car> allCars = carService.getAll();
        System.out.println("All cars: " + allCars);

        car.setModel("X6 New Model");
        Car updatedCar = carService.update(car);
        System.out.println("Updated car: " + updatedCar);

        long carIdToDelete = createdCar.getId();
        carService.delete(carIdToDelete);
        System.out.println("Car deleted with ID: " + carIdToDelete);

        List<Driver> drivers = new ArrayList<>();
        Driver john = new Driver();
        john.setName("John");
        drivers.add(john);

        Driver alice = new Driver();
        alice.setName("Alice");
        drivers.add(alice);

        car.setDrivers(drivers);

        List<Car> allCarsUpdate = carService.getAll();
        System.out.println("All cars: " + allCarsUpdate);
    }
}
