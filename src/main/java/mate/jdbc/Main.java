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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer1 = new Manufacturer();
        manufacturer1.setName("suzuki");
        manufacturer1.setCountry("japan");
        manufacturerService.create(manufacturer1);
        Driver driver1 = new Driver();
        driver1.setName("anton");
        driver1.setLicenseNumber("56473");
        Driver driver2 = new Driver();
        driver2.setName("Fedya");
        driver2.setLicenseNumber("26247");
        Driver driver3 = new Driver();
        driver3.setName("Vasil");
        driver3.setLicenseNumber("13516");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driver1);
        driverService.create(driver2);
        driverService.create(driver3);
        Car car1 = new Car();
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver1);
        drivers.add(driver2);
        car1.setDrivers(drivers);
        car1.setManufacturer(manufacturer1);
        car1.setModel("vitara");
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car1);
        carService.get(car1.getId());
        carService.update(car1);
        carService.getAll();
        carService.addDriverToCar(driver3, car1);
        carService.removeDriverFromCar(driver2, car1);
        System.out.println(carService.getAllByDriver(driver1.getId()));
        carService.delete(car1.getId());
    }
}
