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
    private static Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setCountry("germany");
        bmwManufacturer.setName("bmw");
        Manufacturer audiManufacturer = new Manufacturer();
        audiManufacturer.setCountry("germany");
        audiManufacturer.setName("audi");
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        manufacturerService.create(bmwManufacturer);
        manufacturerService.create(audiManufacturer);
        Driver driver1 = new Driver();
        driver1.setName("Anton");
        driver1.setLicenceNumber("12345");
        Driver driver2 = new Driver();
        driver2.setName("Oleh");
        driver2.setLicenceNumber("67890");
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        driverService.create(driver1);
        driverService.create(driver2);
        List<Driver> driversList1 = new ArrayList<>();
        Car car1 = new Car();
        car1.setModel("530d");
        car1.setManufacturer(bmwManufacturer);
        car1.setDrivers(driversList1);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        carService.addDriverToCar(driver1, car1);
        carService.addDriverToCar(driver2, car1);
        List<Driver> driversList2 = new ArrayList<>();
        Car car2 = new Car();
        car2.setModel("A6 C7");
        car2.setManufacturer(audiManufacturer);
        car2.setDrivers(driversList2);
        carService.create(car2);
        carService.addDriverToCar(driver1, car2);
        System.out.println("carService method get for car1: " + carService.get(car1.getId()));
        System.out.println("carService method getAll:");
        carService.getAll().forEach(System.out::println);
        System.out.println("carService method getAllByDriver for driver1: ");
        carService.getAllByDriver(driver1.getId()).forEach(System.out::println);
        car1.setModel("X5M");
        System.out.println("carService method update for car1: "
                + carService.update(car1));
        carService.removeDriverFromCar(driver1, car1);
        System.out.println("carService method delete for car2: "
                + carService.delete(car2.getId()));
    }
}
