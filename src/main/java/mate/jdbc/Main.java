package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService =
                (CarService) injector.getInstance(CarService.class);

        Car car = new Car();
        car.setModel("Caddy");
        Manufacturer manufacturer = new Manufacturer("Volkswagen", "Germany");
        manufacturer.setId(1L);
        car.setManufacturer(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(5L, "Grigory", "t77"));
        drivers.add(new Driver(2L, "Petro", "t5"));
        car.setDrivers(drivers);
        Car newCar = carService.create(car);
        System.out.println(newCar);

        Car getCar = carService.get(1L);
        System.out.println(getCar);

        car.setId(4L);
        car.setModel("UAZ");
        manufacturer.setId(5L);
        manufacturer.setName("Bobic");
        manufacturer.setCountry("USSR");
        car.setManufacturer(manufacturer);
        drivers = new ArrayList<>();
        drivers.add(new Driver(1L, "Antin", "t8"));
        drivers.add(new Driver(3L, "Vlad", "t10"));
        car.setDrivers(drivers);
        Car updatedCar = carService.update(car);
        System.out.println(updatedCar);

        Driver driver = new Driver(9L, "Oleksandr", "1t");
        carService.addDriverToCar(driver, car);
        System.out.println("Driver " + driver + " added to car " + car);

        carService.removeDriverFromCar(driver, car);
        System.out.println("Driver " + driver + " removed from car " + car);

        Long id = 2L;
        if (carService.delete(id)) {
            System.out.println("Car with id " + id + " deleted from DB");
        }

        Long driverId = 2L;
        List<Car> carsListByDriver = carService.getAllByDriver(driverId);
        System.out.println("List of cars for driver id: " + driverId);
        carsListByDriver.forEach(System.out::println);

        List<Car> carsList = carService.getAll();
        System.out.println("list of cars:");
        carsList.forEach(System.out::println);

    }
}
