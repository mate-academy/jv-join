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
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        //creating manufacturers
        Manufacturer manufacturerToCreate = new Manufacturer("Toyota", "Japan");
        Manufacturer createdManufacturer = manufacturerService.create(manufacturerToCreate);
        //creating Drivers
        Driver driverToCreate1 = new Driver("Joe", "001");
        Driver driverToCreate2 = new Driver("Maria", "002");
        Driver createdDriver1 = driverService.create(driverToCreate1);
        Driver createdDriver2 = driverService.create(driverToCreate2);
        //creating cars
        Car carToCreate1 = new Car("Corolla",
                createdManufacturer,
                new ArrayList<>());
        carToCreate1.getDrivers().add(createdDriver1);
        carToCreate1.getDrivers().add(createdDriver2);
        Car carToCreate2 = new Car("Camry",
                createdManufacturer,
                new ArrayList<>());
        carToCreate2.getDrivers().add(createdDriver1);
        Car carToCreate3 = new Car("Land Cruiser",
                createdManufacturer,
                new ArrayList<>());
        Car createdCar1 = carService.create(carToCreate1);
        Car createdCar2 = carService.create(carToCreate2);
        Car createdCar3 = carService.create(carToCreate3);
        //log cars
        System.out.println(ANSI_GREEN + "First car: " + ANSI_RESET
                + System.lineSeparator()
                + carService.get(createdCar1.getId()).toString());
        System.out.println(ANSI_GREEN + "Second car: " + ANSI_RESET
                + System.lineSeparator()
                + carService.get(createdCar2.getId()).toString());
        System.out.println(ANSI_GREEN + "Third car: " + ANSI_RESET
                + System.lineSeparator()
                + carService.get(createdCar3.getId()).toString());
        //update cars
        Long carToUpdate = carService.get(createdCar2.getId()).getId();
        Car updatedCar1 = new Car(carToUpdate,
                "Supra",
                carService.get(carToUpdate).getManufacturer(),
                carService.get(carToUpdate).getDrivers());
        carService.update(updatedCar1);
        //add driver to a car
        carService.addDriverToCar(createdDriver2, createdCar3);
        //remove driver from a car
        carService.removeDriverFromCar(createdDriver2, createdCar2);
        //delete one of the cars
        carService.delete(createdCar2.getId());
        //log cars of second driver
        System.out.println(ANSI_GREEN + "Maria's cars: " + ANSI_RESET
                + System.lineSeparator()
                + carService.getAllByDriver(createdDriver2.getId()).toString());
        //out result
        System.out.println(ANSI_GREEN + "All cars in DB: " + ANSI_RESET);
        carService.getAll().forEach(System.out::println);
    }
}
