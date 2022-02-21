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
        car.setModel("Polo");
        Manufacturer manufacturer = new Manufacturer("Volkswagen", "Germany");
        manufacturer.setId(1L);
        car.setManufacturer(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(new Driver(6L, "Oleksiy Danchenko", "00000010"));
        drivers.add(new Driver(3L, "Anton Terekhov", "00000002"));
        car.setDrivers(drivers);
        Car savedCar = carService.create(car);
        System.out.println("New car has been created: " + savedCar);

        Car getCar = carService.get(8L);
        System.out.println("Car was received from DB: " + getCar);

        car.setId(8L);
        car.setModel("Jazz");
        manufacturer.setId(5L);
        manufacturer.setName("Honda");
        manufacturer.setCountry("Japan");
        car.setManufacturer(manufacturer);
        drivers = new ArrayList<>();
        drivers.add(new Driver(2L, "Igor Shuliak", "00000003"));
        drivers.add(new Driver(4L, "Taras Shevchenko", "00000005"));
        car.setDrivers(drivers);
        Car updatedCar = carService.update(car);
        System.out.println("Car has been changed: " + updatedCar);

        Driver driver = new Driver(5L, "Oleksandr Fedoruk", "00000006");
        carService.addDriverToCar(driver, car);
        System.out.println("Driver " + driver + " was added to car " + car);

        carService.removeDriverFromCar(driver, car);
        System.out.println("Driver " + driver + " was removed from car " + car);

        Long id = 2L;
        if (carService.delete(id)) {
            System.out.println("Car with id " + id + " was successfully deleted from DB");
        }

        Long driverId = 4L;
        List<Car> carsListByDriver = carService.getAllByDriver(driverId);
        System.out.println("List of cars for driver id: " + driverId);
        carsListByDriver.forEach(System.out::println);

        List<Car> carsList = carService.getAll();
        System.out.println("Full list of cars:");
        carsList.forEach(System.out::println);
    }
}
