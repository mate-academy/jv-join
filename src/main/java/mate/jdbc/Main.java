package mate.jdbc;

import java.util.ArrayList;
import java.util.List;
import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);

    public static void main(String[] args) {
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setCountry("Germany");
        bmwManufacturer.setName("bmwManufacturer");
        manufacturerService.create(bmwManufacturer);

        Driver driver = new Driver();
        driver.setName("Alice");
        driver.setLicenseNumber("123456");
        driver.setId(5L);

        List<Driver> drivers = new ArrayList<>();

        Car bmw = new Car();
        bmw.setModel("bmw");
        bmw.setManufacturer(bmwManufacturer);
        bmw.setDrivers(drivers);
        // CAR_SERVICE ADD DRIVER
        carService.addDriverToCar(driver, bmw);
        System.out.println(bmw);
        // CAR_SERVICE CREATE
        carService.create(bmw);
        // CAR_SERVICE REMOVE DRIVER
        carService.removeDriverFromCar(driver, bmw);
        System.out.println(bmw);
        // CAR_SERVICE GET
        System.out.println(carService.get(bmw.getId()));
        // CAR_SERVICE GET_ALL
        System.out.println(carService.getAll());
        // CAR_SERVICE UPDATE
        bmw.setModel("X5");
        System.out.println(carService.update(bmw));
        // CAR_SERVICE GET_ALL_BY_DRIVER
        System.out.println(carService.getAllByDriver(bmw.getId()));
        // CAR_SERVICE DELETE
        System.out.println(carService.delete(bmw.getId()));
    }
}
