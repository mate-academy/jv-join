package mate.jdbc;

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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);

        // 1. Add a new manufacturer
        Manufacturer newManufacturer = new Manufacturer("FictitiousMotors", "ImaginaryLand");
        manufacturerService.create(newManufacturer);
        System.out.println("Added new manufacturer: " + newManufacturer);

        // 2. Add fictitious drivers
        Driver alice = new Driver("Alice Wonderland", "AW2023");
        Driver bob = new Driver("Bob Builder", "BB2024");  // Змінено номер ліцензії
        driverService.create(alice);
        driverService.create(bob);
        System.out.println("Added new drivers: " + alice + ", " + bob);

        // 3. Add fictitious cars and associate them with drivers
        Car dreamCar = new Car("Dream", newManufacturer);
        Car fantasyCar = new Car("Fantasy", newManufacturer);
        dreamCar.setDrivers(Arrays.asList(alice, bob));
        fantasyCar.setDrivers(Arrays.asList(bob));
        carService.create(dreamCar);
        carService.create(fantasyCar);
        System.out.println("Added new cars: " + dreamCar + ", " + fantasyCar);

        // 4. Update manufacturer details
        newManufacturer.setCountry("FictitiousLand");
        manufacturerService.update(newManufacturer);
        System.out.println("Updated manufacturer: " + newManufacturer);

        // 5. Update driver details
        alice.setLicenseNumber("ALICE2023");
        driverService.update(alice);
        System.out.println("Updated driver Alice: " + alice);

        // 6. Update car details
        dreamCar.setModel("DreamX");
        carService.update(dreamCar);
        System.out.println("Updated car: " + dreamCar);

        // 7. Delete operations
        manufacturerService.delete(newManufacturer.getId());
        driverService.delete(alice.getId());
        driverService.delete(bob.getId());
        carService.delete(dreamCar.getId());
        carService.delete(fantasyCar.getId());
        System.out.println("Deleted the test data.");

        // 8. Retrieve manufacturer, car, and driver by their IDs
        Manufacturer retrievedManufacturer = manufacturerService.get(newManufacturer.getId());
        if (retrievedManufacturer == null) {
            System.out.println("Manufacturer with ID " + newManufacturer.getId() + " not found.");
        }
        Car retrievedCar = carService.get(dreamCar.getId());
        Driver retrievedDriver = driverService.get(alice.getId());
        System.out.println("Retrieved: " + retrievedManufacturer + ", " + retrievedCar + ", " + retrievedDriver);

        // 9. Find all cars of a specific driver
        List<Car> carsOfAlice = carService.getAllByDriver(alice.getId());
        System.out.println("Cars of Alice: " + carsOfAlice);

        // 11. Update driver's name and license number
        alice.setName("Alice Liddell");
        alice.setLicenseNumber("ALICE-NEW-2023");
        driverService.update(alice);
        System.out.println("Updated driver Alice: " + alice);
    }
}

