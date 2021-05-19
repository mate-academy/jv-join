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
        ManufacturerService manufacturerService = (ManufacturerService) injector.getInstance(
                ManufacturerService.class);
        Manufacturer hondaManufacturer = new Manufacturer("Honda", "Japan");
        Manufacturer renaultManufacturer = new Manufacturer("Renault", "France");
        Manufacturer fordManufacturer = new Manufacturer("Ford", "USA");

        manufacturerService.create(hondaManufacturer);
        manufacturerService.create(renaultManufacturer);
        manufacturerService.create(fordManufacturer);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);

        Driver bobDriver = new Driver("Bob", "34523");
        Driver billDriver = new Driver("Bill", "52311");
        Driver aliceDriver = new Driver("Alice", "224465");

        driverService.create(bobDriver);
        driverService.create(billDriver);
        driverService.create(aliceDriver);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(bobDriver);
        drivers.add(billDriver);
        drivers.forEach(System.out::println);
        Car fordMustang = new Car("Mustang", fordManufacturer);
        fordMustang.setDrivers(drivers);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        System.out.println(carService.create(fordMustang));
        Car hondaCivic = new Car("Civic", hondaManufacturer);
        hondaCivic.setDrivers(drivers);
        System.out.println(carService.create(hondaCivic));
        carService.addDriverToCar(aliceDriver, hondaCivic);

        carService.removeDriverFromCar(billDriver, fordMustang);
        System.out.println(carService.get(fordMustang.getId()));
        carService.delete(fordMustang.getId());
        carService.getAll().forEach(System.out::println);
    }
}
