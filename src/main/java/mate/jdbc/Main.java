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
    private static final Injector INJECTOR = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        final ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);
        Driver driver1 = new Driver("Vitaliy", "4432fb1t");
        Driver driver2 = new Driver("Mykyta", "3216f25t");
        driver1 = driverService.create(driver1);
        driver2 = driverService.create(driver2);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);
        Manufacturer manufacturer = new Manufacturer("Mitsubishi", "Japan");
        manufacturer = manufacturerService.create(manufacturer);
        Car car1 = new Car();
        car1.setManufacturer(manufacturer);
        car1.setModel("Eclipse Cross");
        car1.setDrivers(drivers);
        car1 = carService.create(car1);
        System.out.println(carService.get(car1.getId()));
        carService.removeDriverFromCar(driver1, car1);
        System.out.println("Removed driver1 from car1: " + car1);
        driver1.setLicenseNumber("345kg32t");
        carService.addDriverToCar(driver1, car1);
        System.out.println("Add driver to car1: " + car1);
        System.out.println("Adding and removing driver work! It shows that method Update works.");
        Car car2 = new Car();
        car2.setDrivers(drivers);
        car2.setManufacturer(manufacturer);
        car2.setModel("Outlander");
        car2 = carService.create(car2);
        carService.getAll().forEach(System.out::println);
        System.out.println(carService.getAllByDriver(driver2.getId()));
        carService.delete(car2.getId());
        System.out.println(carService.getAll());
    }
}
