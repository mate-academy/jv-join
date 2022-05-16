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
        ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);

        Manufacturer autoZaz = new Manufacturer("Zaz", "Ukraine");
        Manufacturer dodge = new Manufacturer("Dodge", "USA");
        Manufacturer mazda = new Manufacturer("Mazda", "Japan");

        manufacturerService.create(autoZaz);
        manufacturerService.create(dodge);
        manufacturerService.create(mazda);
        System.out.println(manufacturerService.getAll());
        System.out.println("__________________________________________________________________");

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);

        Driver petroVasylenko = new Driver("Petro Vasylenko", "AAA 11111");
        Driver illiaDanchuk = new Driver("Illia Danchuk", "CCC 33333");

        driverService.create(petroVasylenko);
        driverService.create(illiaDanchuk);
        System.out.println(driverService.getAll());
        System.out.println("__________________________________________________________________");

        List<Driver> drivers = new ArrayList<>();
        drivers.add(petroVasylenko);
        drivers.add(illiaDanchuk);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carMazda = new Car(mazda, "6");
        Car carDodge = new Car(dodge, "challenger");
        carDodge.setDrivers(drivers);
        System.out.println(carDodge);
        carService.create(carMazda);
        Car carZaz = new Car(autoZaz, "forza");
        carService.create(carZaz);
        System.out.println(carService.create(carDodge));

        Driver nazarKovalenko = new Driver("Nazar Kovalenko", "BBB 22222");
        driverService.create(nazarKovalenko);
        drivers.add(nazarKovalenko);
        carService.update(carDodge);
        System.out.println(carService.create(carDodge));
    }
}

