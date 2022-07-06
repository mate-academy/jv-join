package mate.jdbc;

import java.util.ArrayList;
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
        Manufacturer firstManufacturer = new Manufacturer();
        firstManufacturer.setName("First manufacturer");
        firstManufacturer.setCountry("Ukraine");
        Manufacturer secondManufacturer = new Manufacturer();
        secondManufacturer.setName("Second manufacturer");
        secondManufacturer.setCountry("Ukraine");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturerService.create(firstManufacturer);
        manufacturerService.create(secondManufacturer);
        manufacturerService.getAll().forEach(System.out::println);
        System.out.println(manufacturerService.get(firstManufacturer.getId()));
        firstManufacturer.setCountry("USA");
        manufacturerService.update(firstManufacturer);
        manufacturerService.delete(secondManufacturer.getId());
        manufacturerService.getAll().forEach(System.out::println);
        Driver firstDriver = new Driver();
        firstDriver.setName("First driver");
        firstDriver.setLicenseNumber("123");
        Driver secondDriver = new Driver();
        secondDriver.setName("Second Driver");
        secondDriver.setLicenseNumber("123");
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.getAll().forEach(System.out::println);
        System.out.println(driverService.get(firstDriver.getId()));
        firstDriver.setLicenseNumber("1234");
        driverService.update(firstDriver);
        driverService.delete(secondDriver.getId());
        driverService.getAll().forEach(System.out::println);
        Car firstCar = new Car();
        firstCar.setModel("first model");
        firstCar.setManufacturer(firstManufacturer);
        List<Driver> firstCarDrivers = new ArrayList<>();
        firstCarDrivers.add(firstDriver);
        firstCar.setDrivers(firstCarDrivers);
        Car secondCar = new Car();
        secondCar.setModel("second model");
        secondCar.setManufacturer(secondManufacturer);
        List<Driver> secondCarDrivers = new ArrayList<>();
        secondCarDrivers.add(firstDriver);
        secondCarDrivers.add(secondDriver);
        secondCar.setDrivers(secondCarDrivers);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(firstCar);
        carService.create(secondCar);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.addDriverToCar(secondDriver, firstCar);
        System.out.println(carService.get(firstCar.getId()));
        carService.removeDriverFromCar(secondDriver, firstCar);
        System.out.println(carService.get(firstCar.getId()));
        firstCar.setModel("Updated model");
        carService.update(firstCar);
        carService.delete(secondCar.getId());
        carService.getAll().forEach(System.out::println);
    }
}
