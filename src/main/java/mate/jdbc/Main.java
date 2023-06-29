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
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("suzuki");
        manufacturer.setCountry("japan");
        manufacturerService.create(manufacturer);
        Driver firstDriver = new Driver();
        firstDriver.setName("anton");
        firstDriver.setLicenseNumber("56473");
        Driver secondDriver = new Driver();
        secondDriver.setName("Fedya");
        secondDriver.setLicenseNumber("26247");
        Driver thirdDriver = new Driver();
        thirdDriver.setName("Vasil");
        thirdDriver.setLicenseNumber("13516");
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);
        Car car = new Car();
        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);
        car.setDrivers(drivers);
        car.setManufacturer(manufacturer);
        car.setModel("vitara");
        carService.create(car);
        carService.get(car.getId());
        carService.update(car);
        carService.getAll();
        carService.addDriverToCar(thirdDriver, car);
        carService.removeDriverFromCar(secondDriver, car);
        System.out.println(carService.getAllByDriver(firstDriver.getId()));
        carService.delete(car.getId());
    }
}
