package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final String COUNTRY_GERMANY = "Germany";
    private static final String BMW_MANUFACTURER = "bmwManufacturer";
    private static final String NAME_ALICE = "Alice";
    private static final String LICENSE_NUMBER = "123456";
    private static final long ID = 5L;
    private static final String NEW_MODEL = "X5";
    private static final String MODEL = "bmw";

    public static void main(String[] args) {
        Manufacturer bmwManufacturer = new Manufacturer();
        bmwManufacturer.setCountry(COUNTRY_GERMANY);
        bmwManufacturer.setName(BMW_MANUFACTURER);
        manufacturerService.create(bmwManufacturer);

        Driver driver = new Driver();
        driver.setName(NAME_ALICE);
        driver.setLicenseNumber(LICENSE_NUMBER);
        driver.setId(ID);

        Car bmw = new Car();
        bmw.setModel(MODEL);
        bmw.setManufacturer(bmwManufacturer);
        bmw.setDrivers(driverService.getAll());
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
        bmw.setModel(NEW_MODEL);
        System.out.println(carService.update(bmw));
        // CAR_SERVICE GET_ALL_BY_DRIVER
        System.out.println(carService.getAllByDriver(bmw.getId()));
        // CAR_SERVICE DELETE
        System.out.println(carService.delete(bmw.getId()));
    }
}
