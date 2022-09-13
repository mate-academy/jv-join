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
    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("America");
        manufacturer.setName("AmericanManufacrturer");
        Driver driver = new Driver();
        driver.setLicenseNumber("7");
        driver.setName("Sany");
        Driver driver1 = new Driver();
        driver1.setName("Carl");
        driver1.setLicenseNumber("4");
        Injector injector = Injector.getInstance("mate.jdbc");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver createdDriver = driverService.create(driver);
        Driver createdDriver1 = driverService.create(driver1);
        Car car = new Car();
        car.setModel("busik");
        car.setDrivers(new ArrayList<>(List.of(createdDriver, createdDriver1)));
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);
        car.setManufacturer(createdManufacturer);
        System.out.println(car);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car createdCar = carService.create(car);
        System.out.println("car was created");
        System.out.println("------------");
        System.out.println("get car by id");
        System.out.println(carService.get(createdCar.getId()));
        System.out.println("------------");
        System.out.println("get all cars by driver id");
        System.out.println(carService.getAllByDriver(createdDriver1.getId()));
        System.out.println("------------");
        System.out.println("created and added new driver to cars driver list");
        Driver driver2 = new Driver();
        driver2.setLicenseNumber("52");
        driver2.setName("Petya");
        Driver createdDriver2 = driverService.create(driver2);
        carService.addDriverToCar(createdDriver2, createdCar);
        System.out.println(carService.get(createdCar.getId()));
        System.out.println("------------");
        System.out.println("remove driver from cars driver list");
        carService.removeDriverFromCar(createdDriver, createdCar);
        System.out.println(carService.get(createdCar.getId()));
        System.out.println("------------");
        System.out.println("updated car");
        Car car1 = new Car();
        car1.setModel("NEW_model1");
        car1.setManufacturer(createdManufacturer);
        car1.setDrivers(new ArrayList<>(List.of(createdDriver1, createdDriver2, createdDriver)));
        System.out.println(carService.update(car1));
        System.out.println("------------");
        System.out.println("deleted and get ALL");
        carService.delete(createdCar.getId());
        System.out.println(carService.getAll());
    }
}

