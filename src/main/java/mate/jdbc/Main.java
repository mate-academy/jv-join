package mate.jdbc;

import java.util.List;
import java.util.Random;
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
        System.out.println("savedManufacturer = " + toyota);
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
        System.out.println("savedDriver = " + bohdan);
        /////getAll drivers/////
        List<Driver> drivers = driverService.getAll();
        drivers.forEach(System.out::println);

        /////////////Car////////////
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        /////create toyota RAV4 /////
        Car createdCar = carService
                .create(new Car("RAV4", manufacturerService.get(toyota.getId())));
        createdCar.setDrivers(drivers);
        System.out.println("createdCar = " + createdCar);
        /////get/////
        Car toyotaRav4 = carService.get(createdCar.getId());
        System.out.println("toyota RAV4 = " + toyotaRav4);
        /////update/////
//        Car updateBmvX5 = new Car(bohdan.getId(), "Bohdan", "951753654");
//        Car updatedCar = carService.update(updateBohdan);
//        System.out.println("updatedCar = " + updatedCar);
        /////delete/////
        boolean isDeletedCar = carService.delete(createdCar.getId());
        System.out.println("deleteCar = " + isDeletedCar);
        /////getAll/////
//        List<Car> cars = carService.getAll();
//        cars.forEach(System.out::println);
    }
}
