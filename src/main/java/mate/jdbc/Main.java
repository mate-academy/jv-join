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
        System.out.println(carService.get(1L));

        Manufacturer manufacturer = manufacturerService.get(8L);
        List<Driver> drivers = List.of(driverService.get(1L), driverService.get(3L));
        Car createdCar = new Car(1L, "Tesla", manufacturer, drivers);
        System.out.println("Created car: " + carService.create(createdCar));

        System.out.println("Car with id 3: " + carService.get(3L));

        System.out.println("Car with id 3 was deleted: " + carService.delete(3L));

        Car updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setModel("New Tesla");
        Manufacturer manufacturerForUpdate = manufacturerService.get(7L);
        updatedCar.setManufacturer(manufacturerForUpdate);
        List<Driver> driversForUpdate = List.of(driverService.get(2L), driverService.get(4L));
        updatedCar.setDrivers(driversForUpdate);
        System.out.println("Updated car: " + carService.update(updatedCar));

        System.out.println("Car before adding a driver: " + carService.get(2L));
        Car carToAdd = carService.get(2L);
        Driver addedDriver = driverService.get(2L);
        carService.addDriverToCar(addedDriver, carToAdd);
        System.out.println("Car after adding a driver: " + carService.get(2L));

        System.out.println("Car before removing a driver: " + carService.get(3L));
        Car carForRemove = carService.get(3L);
        Driver removedDriver = driverService.get(3L);
        carService.removeDriverFromCar(removedDriver, carForRemove);
        System.out.println("Car after removing a driver: " + carService.get(3L));

        List<Car> allByDriver = carService.getAllByDriver(1L);
        System.out.println("All about driver with id 4: " + allByDriver);

        carService.getAll().forEach(System.out::println);
    }
}
