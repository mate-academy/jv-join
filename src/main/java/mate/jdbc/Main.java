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
        Car car = new Car();
        car.setModel("Audi");

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setCountry("Germany");
        manufacturer.setName("Porse Group");

        Driver driver = new Driver();
        driver.setLicenseNumber("Ivan's License");
        driver.setName("Ivan Aldokhin");

        List<Driver> allDrivers = new ArrayList<>();
        allDrivers.add(driver);
        car.setManufacturer(manufacturer);
        car.setDrivers(allDrivers);

        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        CarService carService = (CarService) injector
                .getInstance(CarService.class);

        Manufacturer manufacturerFromService = manufacturerService.create(manufacturer);
        System.out.println(manufacturerFromService);
        Driver driverFromService = driverService.create(driver);
        System.out.println(driverFromService);
        Car carFromService = carService.create(car);
        System.out.println(carFromService);

        Driver driverIgor = new Driver();
        driverIgor.setName("Igor");
        driverIgor.setLicenseNumber("Igor's license");
        Driver driverIgorFromService = driverService.create(driverIgor);

        carService.addDriverToCar(driverIgorFromService,carFromService);
        System.out.println(carFromService);

        carService.removeDriverFromCar(driverFromService,carFromService);
        System.out.println(carFromService);
    }
}
