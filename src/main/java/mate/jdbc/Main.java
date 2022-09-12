package mate.jdbc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
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
        /////////////Manufacturer/////////////
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        /////create toyota/////
        Manufacturer newManufacturer = new Manufacturer();
        newManufacturer.setName("Toyota");
        newManufacturer.setCountry("Japan");
        Manufacturer toyota = manufacturerService.create(newManufacturer);
        System.out.println("createdManufacturer = " + toyota);
          /////getAll manufacturers/////
        List<Manufacturer> manufacturers = manufacturerService.getAll();
        manufacturers.forEach(System.out::println);

        /////////////Driver////////////
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        /////create driver/////
        Driver newDriver = new Driver();
        newDriver.setName("Bohdan");
        newDriver.setLicenseNumber(String.valueOf(new Random().nextInt(999999999)));
        Driver bohdan = driverService.create(newDriver);
        System.out.println("createdDriver = " + bohdan);
        /////getAll drivers/////
        System.out.println("-------getAllDrivers-------");
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);

        /////////////Car////////////
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        /////create toyota RAV4 /////
        Car newCarToyotaRav4 = new Car();
        newCarToyotaRav4.setManufacturer(manufacturerService.get(toyota.getId()));
        newCarToyotaRav4.setModel("RAV4");
        newCarToyotaRav4.setDrivers(new HashSet<>(drivers));
        Car createdCar = carService
                .create(newCarToyotaRav4);
        System.out.println("createdCar = " + createdCar);
        /////get/////
        Car toyotaRav4 = carService.get(createdCar.getId());
        System.out.println("getCar = " + toyotaRav4);
        /////update/////
//        Car updateBmvX5 = new Car(bohdan.getId(), "Bohdan", "951753654");
//        Car updatedCar = carService.update(updateBohdan);
//        System.out.println("updatedCar = " + updatedCar);
        /////delete/////
//        boolean isDeletedCar = carService.delete(createdCar.getId());
//        System.out.println("deleteCar = " + isDeletedCar);
        /////getAll/////
        System.out.println("-------getAllCars-------");
        List<Car> cars = carService.getAll();
        cars.forEach(System.out::println);
        /////addDriverToCar/////
        newDriver.setName("Bob");
        newDriver.setLicenseNumber(String.valueOf(new Random().nextInt(999999999)));
        Driver bob = driverService.create(newDriver);
        System.out.println("createdDriver = " + bob);
        carService.addDriverToCar(bob, toyotaRav4);
        System.out.println("toyotaRav4.allDrivers = " + carService.get(toyotaRav4.getId()));
        carService.addDriverToCar(bob, toyotaRav4);
        carService.addDriverToCar(bob, toyotaRav4);
        carService.addDriverToCar(bob, toyotaRav4);
        System.out.println("toyotaRav4.allDrivers = " + carService.get(toyotaRav4.getId()));
//        System.out.p//rintln("toyotaRav4 = " + toyotaRav4);
        /////removeDriverFromCar/////

        /////getAllByDriver/////

    }
}
