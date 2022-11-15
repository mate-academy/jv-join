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
        final CarService carService = (CarService)
                injector.getInstance(CarService.class);
        final DriverService driverService = (DriverService)
                injector.getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService)
                injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Audi");
        manufacturer.setCountry("Germany");
        Manufacturer audi = manufacturerService.create(manufacturer);
        List<Driver> drivers = driverService.getAll();
        Car car = new Car();
        car.setManufacturer(audi);
        car.setDrivers(drivers);
        car.setModel("A1");
        Car audiA1 = carService.create(car);
        Driver driver = new Driver();
        driver.setName("Bob");
        driver.setLicenseNumber("1234");
        Driver bob = driverService.create(driver);
        carService.addDriverToCar(bob, audiA1);
        carService.removeDriverFromCar(bob, audiA1);
        List<Car> cars = carService.getAllByDriver(bob.getId());
        System.out.println(cars);
        carService.delete(audiA1.getId());
    }
}
