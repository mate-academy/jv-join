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
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setName("Audi");
        manufacturer.setCountry("Germany");
        manufacturerService.create(manufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver();
        firstDriver.setName("Andrey");
        firstDriver.setLicenseNumber("73QR4P");
        driverService.create(firstDriver);

        Driver secondDriver = new Driver();
        secondDriver.setName("Zakhar");
        secondDriver.setLicenseNumber("777WY2");
        driverService.create(secondDriver);

        Driver thirdDriver = new Driver();
        thirdDriver.setName("Roman");
        thirdDriver.setLicenseNumber("34TV12");
        driverService.create(thirdDriver);

        List<Driver> drivers = new ArrayList<>();
        drivers.add(firstDriver);
        drivers.add(secondDriver);

        Car car = new Car();
        car.setModel("A3");
        car.setManufacturer(manufacturer);
        car.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(car);

        carService.get(car.getId());

        car.setModel("A4");
        carService.update(car);

        carService.addDriverToCar(thirdDriver, car);

        carService.removeDriverFromCar(secondDriver, car);

        carService.getAll().forEach(System.out::println);

        carService.delete(car.getId());
    }
}
