package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.*;

import java.util.List;

public class Main {
    public static final Injector injector = Injector.getInstance("mate.jdbc");
    public static void main(String[] args) {
        //driver create
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver adolf = new Driver("Adolf", "399188374");
        Driver mykola = new Driver("Mykola", "84991849");
        Driver olena = new Driver("Olena", "048910948");
        driverService.create(adolf);
        driverService.create(mykola);
        driverService.create(olena);
        //manufacturer create
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        List<Manufacturer> manufacturers = List.of(
                new Manufacturer("BanderoMobile", "Ukraine"),
                new Manufacturer("Tesla", "USA"),
                new Manufacturer("Hyundai", "Japan"));
        manufacturers.forEach(manufacturerService::create);
        //testing car
        CarService carService = (CarService) injector.getInstance(CarService.class);
        List<Car> cars = List.of(
                new Car("Tesla model Y 2017", manufacturers.get(1), List.of(adolf, mykola)),
                new Car("Hyundai Sonata 2010", manufacturers.get(2), List.of(olena)));
        cars.forEach(carService::create);
        carService.getAll().forEach(System.out::println);
        Car car = carService.get(cars.stream().findFirst().get().getId());
        System.out.println(System.lineSeparator());
        System.out.println(car);
        Driver newDriver = new Driver("Peter", "999348112");
        driverService.create(newDriver);
        carService.addDriverToCar(newDriver, car);
        System.out.println(System.lineSeparator());
        System.out.println(carService.get(car.getId()));
        carService.removeDriverFromCar(adolf, cars.get(1));
        System.out.println(System.lineSeparator());
        System.out.println(carService.get(cars.get(0).getId()));
        carService.delete(cars.stream()
                .findAny().get().getId());
        System.out.println(carService.getAllByDriver(cars.get(0).getId()));
    }
}
