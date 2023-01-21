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

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setId(13L);
        manufacturer.setName("BMW");
        manufacturer.setCountry("Germany");
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturer = manufacturerService.create(manufacturer);
        Driver driver1 = new Driver();
        driver1.setId(1L);
        driver1.setName("Vasyl Moshun");
        driver1.setLicenseNumber("nvrv21rrc");
        Driver driver2 = new Driver();
        driver2.setId(2L);
        driver2.setName("Olena");
        driver2.setLicenseNumber("vrvre12vev");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driver1 = driverService.create(driver1);
        driver2 = driverService.create(driver2);
        Car car = new Car();
        car.setId(1L);
        car.setModel("f11");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driver1, driver2));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("g10");
        car = carService.update(car);
        carService.addDriverToCar(driver2, car);
        carService.removeDriverFromCar(driver2, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(2L);
        carService.getAll().forEach(System.out::println);
    }
}
