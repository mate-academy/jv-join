package mate.jdbc;

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

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.getAll().forEach(System.out::println);

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driverService.getAll().forEach(System.out::println);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        carService.getAll().forEach(System.out::println);

        Manufacturer oldTeslaManufacturer = new Manufacturer("Tesla Motors", "USA");
        Manufacturer createdOldManufacturer = manufacturerService.create(oldTeslaManufacturer);
        manufacturerService.getAll().forEach(System.out::println);

        Driver driverJohn = new Driver("John", "X5X6S1");
        Driver driverMark = new Driver("Mark", "V9X7L1");
        Driver driverSem = new Driver("Sem", "R4D7K6");
        Driver createdDriverJohn = driverService.create(driverJohn);
        Driver createdDriverMark = driverService.create(driverMark);
        Driver createdDriverSem = driverService.create(driverSem);

        Car createdCar = new Car("Tesla", createdOldManufacturer, List.of(createdDriverJohn));
        System.out.println("Created car: " + carService.create(createdCar));

        System.out.println("Created car: " + carService.get(createdCar.getId()));

        System.out.println("Created car was deleted: " + carService.delete(createdCar.getId()));

        Manufacturer newTeslaManufacturer = new Manufacturer("Tesla, Inc.", "USA");
        Manufacturer createdNewManufacturer = manufacturerService.create(newTeslaManufacturer);
        Car updatedCar = new Car("New Tesla", createdNewManufacturer, List.of(createdDriverMark));
        System.out.println("Updated car: " + carService.update(updatedCar));

        System.out.println("Car before adding a driver: " + carService.get(updatedCar.getId()));
        Car carToAdd = carService.get(updatedCar.getId());
        Driver addedDriver = driverService.get(createdDriverSem.getId());
        carService.addDriverToCar(addedDriver, carToAdd);
        System.out.println("Car after adding a driver: " + carService.get(updatedCar.getId()));

        System.out.println("Car before removing a driver: " + carService.get(updatedCar.getId()));
        Car carForRemove = carService.get(createdDriverMark.getId());
        Driver removedDriver = driverService.get(createdDriverMark.getId());
        carService.removeDriverFromCar(removedDriver, carForRemove);
        System.out.println("Car after removing a driver: " + carService.get(updatedCar.getId()));

        List<Car> allByDriver = carService.getAllByDriver(createdDriverJohn.getId());
        System.out.println("All about driver with id 4: " + allByDriver);

        carService.getAll().forEach(System.out::println);
    }
}
