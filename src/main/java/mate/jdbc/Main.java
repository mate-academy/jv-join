package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) injector
                .getInstance(CarService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final Driver driverTaxi = new Driver(1L, "Volodymyr", "1234");
        Car car = new Car();
        car.setModel("525");
        car.setManufacturer(manufacturerService.get(8L));
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(3L, "Bob", "4321"));
        car.setDrivers(drivers);
        System.out.println(carService.create(car));

        Car retrievedCar = carService.get(car.getId());
        System.out.println(retrievedCar);
        carService.getAll().forEach(System.out::println);

        Car carUpdate = new Car();
        List<Driver> updatedDrivers = new ArrayList<>();
        updatedDrivers.add(new Driver(5L, "Bob", "4321"));
        carUpdate.setId(retrievedCar.getId());
        carUpdate.setModel("328");
        carUpdate.setManufacturer(new Manufacturer(16L, "GMC", "USA"));
        carUpdate.setDrivers(updatedDrivers);
        System.out.println(carService.update(carUpdate));

        System.out.println(carService.delete(retrievedCar.getId()));
        carService.addDriverToCar(driverTaxi, carUpdate);
        carService.removeDriverFromCar(driverTaxi, carUpdate);
        carService.getAllByDriver(driverTaxi.getId()).forEach(System.out::println);
        carService.addDriverToCar(driverTaxi, car);
        System.out.println(car);
        carService.removeDriverFromCar(driverTaxi, car);
        System.out.println(car);
    }
}
