package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        List<Driver> drivers = List.of(
                new Driver("Kim", "АН7625ВС"),
                new Driver("Jack", "АХ1234ВС"),
                new Driver("Bob", "КХ7812ПМ"),
                new Driver("John", "КХ1251НВ"));
        for (Driver driver: drivers) {
            driverService.create(driver);
        }

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = List.of(new Manufacturer("Bob", "France"),
                new Manufacturer("BMW", "Germany"),
                new Manufacturer("Reno", "France"),
                new Manufacturer("Ford", "USA"));
        for (Manufacturer manufacturer: manufacturers) {
            manufacturerService.create(manufacturer);
        }

        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = new ArrayList<>();
        List<Driver> driverListFirstCar = drivers.stream()
                .filter(driver -> driver.getName().startsWith("J")).collect(Collectors.toList());
        List<Driver> driverListSecondCar = drivers.stream()
                .filter(driver -> !driver.getName().startsWith("J")).collect(Collectors.toList());
        cars.add(new Car("Ferrari", manufacturers.get(1), driverListFirstCar));
        cars.add(new Car("Honda", manufacturers.get(3), driverListSecondCar));
        for (Car car: cars) {
            carService.create(car);
        }
        carService.getAll().forEach(car -> System.out.println("1. Model - " + car.getModel()
                + "\n2. Drivers: " + car.getDrivers()));
        Car car = carService.get(cars.stream().findFirst().get().getId());
        System.out.println("1. Model - " + car.getModel()
                + "\n2. Drivers: " + car.getDrivers());
        carService.addDriverToCar(driverService.create(new Driver("Misha", "АХ1235РП")), car);
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(drivers.get(1), cars.get(1));
        System.out.println(carService.get(cars.get(1).getId()));
        carService.delete(cars.stream().findFirst().get().getId());
        carService.getAll().forEach(auto -> System.out.println("1. Model - " + car.getModel()
                + "\n2. Drivers: " + car.getDrivers()));
        List<Car> allByDriver = carService.getAllByDriver(cars.get(1).getId());
        allByDriver.forEach(auto -> System.out.println("1. Model - " + car.getModel()
                + "\n2. Drivers: " + car.getDrivers()));
    }
}
