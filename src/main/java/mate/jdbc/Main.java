package mate.jdbc;

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
    private static final ManufacturerService manufacturerService = (ManufacturerService)
            injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService = (DriverService)
            injector.getInstance(DriverService.class);

    public static void main(String[] args) {
        // test your code here
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("BMW");
        manufacturer.setCountry("Germany");
        manufacturerService.create(manufacturer);

        Driver driver = new Driver();
        driver.setName("John");
        driver.setLicenseNumber("1234");
        driverService.create(driver);

        Driver driver2 = new Driver();
        driver2.setName("Smit");
        driver2.setLicenseNumber("5678");
        driverService.create(driver2);

        Car car = new Car();
        car.setModel("M5");
        car.setManufacturer(manufacturerService.get(1L));
        car.setDrivers(List.of(driverService.get(1L), driverService.get(2L)));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        System.out.println(carService.get(1L));
        System.out.println(carService.getAll());

        System.out.println(carService.update(car));
        carService.delete(1L);
        System.out.println(carService.getAllByDriver(1L));
    }
}
