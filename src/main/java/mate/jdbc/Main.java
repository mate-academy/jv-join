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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer exitManufacturer = manufacturerService.create(getNewManufacturer());
        exitManufacturer.setCountry("Poland");
        manufacturerService.update(exitManufacturer);
        manufacturerService.delete(exitManufacturer.getId());
        System.out.println(manufacturerService.getAll());

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver exitDriver = driverService.create(getNewDriver());
        driverService.update(exitDriver);
        driverService.delete(exitDriver.getId());
        System.out.println(driverService.getAll());

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car car = getNewCar(exitManufacturer);

        List<Driver> driverList = new ArrayList<>();
        driverList.add(exitDriver);
        car.setDrivers(driverList);

        Car exitCar = carService.create(car);
        carService.addDriverToCar(exitDriver, exitCar);
        carService.getAllByDriver(exitDriver.getId());
        carService.removeDriverFromCar(exitDriver, exitCar);
        carService.delete(exitCar.getId());
        carService.getAll();
    }

    private static Manufacturer getNewManufacturer() {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Germany");
        manufacturer.setName("Audi");
        return manufacturer;
    }

    private static Driver getNewDriver() {
        Driver driver = new Driver();
        driver.setName("Bob Dilan");
        driver.setLicenseNumber("124356AV");
        return driver;
    }

    private static Car getNewCar(Manufacturer manufacturer) {
        Car car = new Car();
        car.setModel("Audi A6");
        car.setManufacturer(manufacturer);
        return car;
    }
}
