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
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer teslaManufacturer = new Manufacturer("Tesla", "USA");
        Manufacturer hondaManufacturer = new Manufacturer("Honda Motor", "Japan");
        manufacturerService.create(teslaManufacturer);
        manufacturerService.create(hondaManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bobDriver = new Driver("Bob", "1234567890");
        Driver nickDriver = new Driver("Nick", "9876543210");
        Driver maryDriver = new Driver("Mary", "7419632580");
        Driver annaDriver = new Driver("Anna", "8462791530");
        driverService.create(bobDriver);
        driverService.create(nickDriver);
        driverService.create(maryDriver);
        driverService.create(annaDriver);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car teslaModelSCar = new Car("Model S", teslaManufacturer,
                List.of(bobDriver, maryDriver));
        Car hondaCrzCar = new Car("SR-Z", hondaManufacturer,
                List.of(nickDriver, maryDriver));
        carService.create(teslaModelSCar);
        carService.create(hondaCrzCar);

        System.out.println("- All cars:");
        carService.getAll().forEach(System.out::println);

        System.out.println("- Updated Tesla car:");
        carService.removeDriverFromCar(nickDriver, teslaModelSCar);
        carService.addDriverToCar(annaDriver, teslaModelSCar);
        System.out.println(carService.get(teslaModelSCar.getId()));

        System.out.println("- All cars with Mary driver:");
        carService.getAllByDriver(maryDriver.getId()).forEach(System.out::println);
    }
}
