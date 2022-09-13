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
        Injector injector = Injector.getInstance("mate.jdbc");
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(1L);
        manufacturer.setCountry("America");
        manufacturer.setName("AmericanManufacrturer");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(manufacturer);
        Driver driver = new Driver();
        driver.setId(1L);
        driver.setLicenseNumber("7");
        driver.setName("Sany");
        Driver driver1 = new Driver();
        driver1.setId(2L);
        driver1.setName("Carl");
        driver1.setLicenseNumber("4");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driver);
        driverService.create(driver1);
        Car car = new Car();
        car.setId(4L);
        car.setModel("busik");
        car.setDrivers(new ArrayList<>(List.of(driver, driver1)));
        car.setManufacturer(manufacturer);
        System.out.println(car);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println("car was created");
        System.out.println("------------");
        System.out.println("get car by id");
        System.out.println(carService.get(car.getId()));
        System.out.println("------------");
        System.out.println("get all cars by driver id");
        System.out.println(carService.getAllByDriver(driver1.getId()));
        System.out.println("------------");
        System.out.println("created and added new driver to cars driver list");
        Driver driver2 = new Driver();
        driver2.setLicenseNumber("52");
        driver2.setName("Petya");
        driver2.setId(6L);
        driverService.create(driver2);
        carService.addDriverToCar(driver2, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("------------");
        System.out.println("remove driver from cars driver list");
        carService.removeDriverFromCar(driver, car);
        System.out.println(carService.get(car.getId()));
        System.out.println("------------");
        System.out.println("updated car");
        Car car1 = new Car();
        car1.setModel("NEW_model1");
        car1.setManufacturer(manufacturer);
        car1.setDrivers(new ArrayList<>(List.of(driver1, driver2, driver)));
        System.out.println(carService.update(car1));
        System.out.println("------------");
        System.out.println("deleted and get ALL");
        carService.delete(car.getId());
        System.out.println(carService.getAll());
    }
}

