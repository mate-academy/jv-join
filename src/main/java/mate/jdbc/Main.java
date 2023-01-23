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
        manufacturer.setName("Lexus");
        manufacturer.setCountry("Japan");
        ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        manufacturer = manufacturerService.create(manufacturer);
        Driver driverOne = new Driver();
        driverOne.setId(1L);
        driverOne.setName("Andrey");
        driverOne.setLicenseNumber("HU45RE345467");
        Driver driverTwo = new Driver();
        driverTwo.setId(2L);
        driverTwo.setName("Oleg");
        driverTwo.setLicenseNumber("JUT45356iI96");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverOne = driverService.create(driverOne);
        driverTwo = driverService.create(driverTwo);
        Car car = new Car();
        car.setId(1L);
        car.setModel("UNN77");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driverOne, driverTwo));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car.setModel("KJ45vh5");
        car = carService.update(car);
    }
}
