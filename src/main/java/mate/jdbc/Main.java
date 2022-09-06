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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Manufacturer toyota = manufacturerService.get(1L);
        Driver tom = driverService.get(1L);
        Driver jerry = driverService.get(2L);
        Car car = new Car();
        car.setModel("Camry");
        car.setManufacturer(toyota);
        car.setDrivers(List.of(tom, jerry));
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        car = carService.createCar(car);
        System.out.println(car.toString());
        car = carService.get(4L);
        System.out.println(car.toString());
        Car newCar = new Car();
        newCar.setModel("Camry");
        newCar.setManufacturer(toyota);
        newCar.setDrivers(List.of(tom));
        Car carToUpdate = carService.createCar(newCar);
        carToUpdate.setModel("RAV4");
        carToUpdate.setDrivers(List.of(tom, jerry));
        Car updatedCar = carService.update(carToUpdate);
        System.out.println(updatedCar.toString());
        Car carToDelete = new Car();
        carToDelete.setModel("Focus");
        Manufacturer ford = new Manufacturer();
        ford.setId(3L);
        ford.setName("Ford");
        ford.setCountry("USA");
        carToDelete.setManufacturer(ford);
        Driver john = new Driver();
        john.setId(3L);
        john.setName("John");
        john.setLicenseNumber("0003");
        carToDelete.setDrivers(List.of(john));
        carToDelete = carService.createCar(carToDelete);
        carService.delete(carToDelete.getId());
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        car = carService.get(1L);
        Driver sam = new Driver();
        sam.setId(4L);
        sam.setName("Sam");
        sam.setLicenseNumber("0004");
        carService.addDriverToCar(sam, car);
        carService.removeDriverFromCar(tom, car);
        cars = carService.getAll();
        cars.forEach(System.out::println);
        List<Car> samCars = carService.getAllByDriver(sam.getId());
        samCars.forEach(System.out::println);
    }
}
