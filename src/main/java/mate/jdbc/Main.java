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
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = manufacturerService.create(getNewManufacturer());
        DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        Driver driver = driverService.create(getNewDriver());
        CarService carService = (CarService)
                injector.getInstance(CarService.class);
        Car car = getNewCar(manufacturer);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(driver);
        car.setDrivers(drivers);
        Car car2 = carService.create(car);
        carService.addDriverToCar(driver, car);
        carService.getAllByDriver(2L);
        carService.removeDriverFromCar(driver, car);
        carService.delete(car2.getId());
        carService.getAll();
    }

    private static Manufacturer getNewManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Skoda");
        manufacturer.setCountry("Czech");
        return manufacturer;
    }

    private static Driver getNewDriver() {
        Driver driver = new Driver();
        driver.setName("Bohdan");
        driver.setLicenseNumber("fg8765");
        return driver;
    }

    private static Car getNewCar(Manufacturer manufacturer) {
        Car car = new Car();
        car.setModel("Octavia");
        car.setManufacturer(manufacturer);
        return car;
    }
}
