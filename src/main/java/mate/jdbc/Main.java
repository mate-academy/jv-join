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
        Manufacturer audi = new Manufacturer("Audi", "Germany");
        Manufacturer subaru = new Manufacturer("Subaru", "Japan");
        manufacturerService.create(audi);
        manufacturerService.create(subaru);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver firstDriver = new Driver("Nolan Browning", "529514503");
        Driver secondDriver = new Driver("Drew Torres", "25223198785");
        Driver thirdDriver = new Driver("Dominic Holloway", "5605852006");
        Driver fourthDriver = new Driver("Gordon Freeman", "802552525");
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);
        driverService.create(fourthDriver);

        List<Driver> subaruLegacyDrivers = new ArrayList<>();
        subaruLegacyDrivers.add(secondDriver);
        subaruLegacyDrivers.add(fourthDriver);
        List<Driver> audiQ3Drivers = new ArrayList<>();
        audiQ3Drivers.add(firstDriver);
        audiQ3Drivers.add(thirdDriver);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car subaruLegacy = new Car("Legacy", subaru, subaruLegacyDrivers);
        Car audiQ3 = new Car("Q3", audi, audiQ3Drivers);
        carService.create(subaruLegacy);
        carService.create(audiQ3);

        carService.getAllByDriver(firstDriver.getId());
        carService.get(audiQ3.getId());
        carService.removeDriverFromCar(fourthDriver, subaruLegacy);
        carService.addDriverToCar(firstDriver, subaruLegacy);
        System.out.println("All cars by driver " + firstDriver);
        System.out.println();
        carService.getAll().forEach(System.out::println);
    }
}
