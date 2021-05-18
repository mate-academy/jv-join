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
        Manufacturer toyota = new Manufacturer("Toyota", "Japan");
        Manufacturer peugeot = new Manufacturer("Peugeot", "France");
        Manufacturer ford = new Manufacturer("Ford", "USA");
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        manufacturerService.create(toyota);
        manufacturerService.create(peugeot);
        manufacturerService.create(ford);

        Driver vipDriver = new Driver("Joseph Biden", "000001");
        Driver commonDriver = new Driver("Ivan Ivanov", "123456");
        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(vipDriver);
        driverService.create(commonDriver);

        Car toyotaCorolla = new Car("Corolla", toyota);
        List<Driver> drivers = new ArrayList<>();
        drivers.add(vipDriver);
        drivers.add(commonDriver);
        toyotaCorolla.setDrivers(drivers);
        Car boxerPeugeot = new Car("Boxer", peugeot);
        Car fiestaFord = new Car("Fiesta", ford);
        CarService carService = (CarService) injector.getInstance(CarService.class);
        carService.create(toyotaCorolla);
        carService.create(boxerPeugeot);
        carService.create(fiestaFord);
        carService.addDriverToCar(vipDriver, fiestaFord);
        carService.addDriverToCar(commonDriver, boxerPeugeot);
        carService.removeDriverFromCar(commonDriver, toyotaCorolla);

        System.out.println(carService.getAll());
    }
}
