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
        Driver driverOne = new Driver();
        driverOne.setId(1L);
        driverOne.setName("Vasyl Moshun");
        driverOne.setLicenseNumber("nvrv21rrc");
        Driver driverTwo = new Driver();
        driverTwo.setId(2L);
        driverTwo.setName("Olena");
        driverTwo.setLicenseNumber("vrvre12vev");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverOne = driverService.create(driverOne);
        driverTwo = driverService.create(driverTwo);
        Car car = new Car();
        car.setId(1L);
        car.setModel("f11");
        car.setManufacturer(manufacturer);
        car.setDrivers(List.of(driverOne, driverTwo));
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);
        car = carService.get(1L);
        car.setModel("g10");
        car = carService.update(car);
        carService.addDriverToCar(driverTwo, car);
        carService.removeDriverFromCar(driverTwo, car);
        carService.getAll().forEach(System.out::println);
        carService.getAllByDriver(1L).forEach(System.out::println);
        carService.delete(2L);
        carService.getAll().forEach(System.out::println);
    }
}
