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

        DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);

        Driver petroVasylenko = new Driver("Petro Vasylenko", "AAA 11111");
        Driver illiaDanchuk = new Driver("Illia Danchuk", "CCC 33333");

        driverService.create(petroVasylenko);
        driverService.create(illiaDanchuk);
        System.out.println(driverService.getAll());
        System.out.println("__________________________________________________________________");

        List<Driver> drivers = new ArrayList<>();

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carMazda = new Car(mazda, "6");
        carMazda.setDrivers(drivers);
        System.out.println(carMazda.getDrivers());
        Car carDodge = new Car(dodge, "challenger");
        carDodge.setDrivers(drivers);
        carService.create(carMazda);
        carMazda.setDrivers(drivers);
        Car carZaz = new Car(autoZaz, "forza");
        carService.create(carZaz);
        System.out.println(carService.getAll());

        List <Driver> carZazDrivers = new ArrayList<>();
        carZazDrivers.add(illiaDanchuk);
        carZazDrivers.add(petroVasylenko);
        carService.update(carZaz);
        System.out.println(carZaz);
        carZaz.getDrivers().addAll(carZazDrivers);
        Driver nazarKovalenko = new Driver("Nazar Kovalenko", "BBB 22222");
        driverService.create(nazarKovalenko);
        carService.addDriverToCar(nazarKovalenko, carZaz);
        carService.removeDriverFromCar(illiaDanchuk, carZaz);
        System.out.println(carZaz);

        System.out.println(carService.delete(carMazda.getId()));
        System.out.println("__________________________________________________________________");

        System.out.println(carService.get(carZaz.getId()));
        System.out.println("__________________________________________________________________");
        System.out.println(carMazda.getDrivers());
    }
}
