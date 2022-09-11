package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.CarServiceImpl;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Ford");
        manufacturer.setCountry("USA");
        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);
        System.out.println(createdManufacturer);

        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Chris");
        driver.setLicenseNumber("us123r");
        Driver createdDriver = driverService.create(driver);
        System.out.println(createdDriver);

        //create() CAR
        final CarService carService = (CarService) injector
                .getInstance(CarService.class);
        Car car = new Car();
        car.setModel("Fiesta");
        car.setManufacturer(createdManufacturer);
        car.setDrivers(List.of(driver));
        Car createdCar = carService.create(car);
        System.out.println(createdCar);

        //get()
        Car getCarById = carService.get(createdCar.getId());
        System.out.println(getCarById);

        //delete()

    }
}
