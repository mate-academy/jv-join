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
        Manufacturer audiManufacturer = new Manufacturer("Audi", "Germany");
        Manufacturer renaultManufacturer = new Manufacturer("Renault", "France");
        manufacturerService.create(audiManufacturer);
        manufacturerService.create(renaultManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver bobDriver = new Driver("Bob", "qu13456");
        Driver shoniDriver = new Driver("Shoni", "du12569");
        Driver aliceDriver = new Driver("Alice", "fr10573");
        Driver korchiDriver = new Driver("Korchi", "iv18389");
        driverService.create(bobDriver);
        driverService.create(aliceDriver);
        driverService.create(shoniDriver);
        driverService.create(korchiDriver);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car audiCar = new Car("A7", audiManufacturer, List.of(shoniDriver, korchiDriver));
        Car renaultCar = new Car("Laguna", renaultManufacturer, List.of(aliceDriver, bobDriver));
        carService.create(audiCar);
        carService.create(renaultCar);
        carService.removeDriverFromCar(shoniDriver, audiCar);
        carService.addDriverToCar(shoniDriver, renaultCar);
        carService.get(audiCar.getId());
        carService.getAllByDriver(aliceDriver.getId()).forEach(System.out::println);
    }
}
