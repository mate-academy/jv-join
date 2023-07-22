package mate.jdbc;

import java.util.ArrayList;
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
        ManufacturerService manufacturerService =
                (ManufacturerService) INJECTOR.getInstance(ManufacturerService.class);

        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Honda Japan LTD");
        manufacturer.setCountry("Japan");

        Manufacturer createdManufacturer = manufacturerService.create(manufacturer);

        Car honda = new Car();
        honda.setModel("RCV");
        honda.setManufacturer(createdManufacturer);

        DriverService driverService = (DriverService) INJECTOR.getInstance(DriverService.class);
        Driver driver = new Driver();
        driver.setName("Anton Uzhva");
        driver.setLicenseNumber("9001");
        Driver createdDriver = driverService.create(driver);

        CarService carService = (CarService) INJECTOR.getInstance(CarService.class);
        honda.setDrivers(new ArrayList<>());
        Car createdCar = carService.create(honda);

        carService.addDriverToCar(driverService.get(createdDriver.getId()), createdCar);
        carService.removeDriverFromCar(driverService.get(createdDriver.getId()), createdCar);

        carService.getAllByDriver(createdDriver.getId()).forEach(System.out::println);
    }
}
