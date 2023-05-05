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
        System.out.println("---------- MANUFACTURER ----------");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);

        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setName("BMW");
        newManufacturer.setCountry("Germany");
        Manufacturer createdManufacturer = manufacturerService.create(newManufacturer);
        System.out.println(".create() manufacturer: " + createdManufacturer
                + System.lineSeparator());

        System.out.println("---------- DRIVER ----------");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver firstDriver = new Driver();
        firstDriver.setName("Howard Hamlin");
        firstDriver.setLicenseNumber("331155690");
        Driver createdDriver = driverService.create(firstDriver);
        System.out.println(".create() driver: " + createdDriver + System.lineSeparator());

        Driver secondDriver = new Driver();
        secondDriver.setName("Saul Goodman");
        secondDriver.setLicenseNumber("331155699");
        driverService.create(secondDriver);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);

        System.out.println("---------- CAR ----------");
        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car newCar = new Car();
        newCar.setManufacturer(createdManufacturer);
        newCar.setModel("X7");
        newCar.setDrivers(drivers);

        Car createdCar = carService.create(newCar);
        System.out.println(".create() car: " + createdCar + System.lineSeparator());

        System.out.println("Before .addDriversToCar() for car: " + newCar + System.lineSeparator());
        carService.addDriverToCar(secondDriver, newCar);
        System.out.println(".addDriverToCar(): " + newCar + System.lineSeparator());

        Long createdCarId = createdCar.getId();
        Car gotCarFromDB = carService.get(createdCarId);
        System.out.println(".get() by id: " + gotCarFromDB + System.lineSeparator());

        System.out.println(".getAll() from DB:");
        carService.getAll().forEach(System.out::println);

        Car updateCar = new Car();
        updateCar.setManufacturer(createdManufacturer);
        updateCar.setModel("Z1");
        updateCar.setDrivers(List.of(firstDriver));
        updateCar.setId(createdCarId);
        System.out.println(System.lineSeparator() + ".update(): with " + updateCar);
        System.out.println("Old car: " + gotCarFromDB);
        Car updatedCar = carService.update(updateCar);
        System.out.println("Updated car: " + updatedCar + System.lineSeparator());

        Car secondCar = new Car();
        secondCar.setManufacturer(createdManufacturer);
        secondCar.setModel("M1");
        Car thirdCar = new Car();
        thirdCar.setManufacturer(createdManufacturer);
        thirdCar.setModel("X3");
        secondCar.setDrivers(List.of(firstDriver));
        thirdCar.setDrivers(List.of(secondDriver));
        carService.create(secondCar);
        carService.create(thirdCar);

        List<Car> allByDriver = carService.getAllByDriver(firstDriver.getId());
        System.out.println(".getAllByDriver(): " + allByDriver + System.lineSeparator());

        carService.addDriverToCar(firstDriver, createdCar);
        System.out.println("Car before .removeDriverFromCar(): "
                + createdCar + System.lineSeparator());
        carService.removeDriverFromCar(firstDriver, createdCar);
        System.out.println("Car after .removeDriverFromCar(): "
                + createdCar + System.lineSeparator());

        boolean isDeletedCar = carService.delete(createdCarId);
        System.out.println(".delete(): for " + updatedCar
                + " is " + isDeletedCar + System.lineSeparator());

        System.out.println(".getAll() after .delete() operation: ");
        carService.getAll().forEach(System.out::println);
    }
}
