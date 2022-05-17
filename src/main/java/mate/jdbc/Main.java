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
        Driver nazarKovalenko = new Driver("Nazar Kovalenko", "BBB 22222");

        driverService.create(petroVasylenko);
        driverService.create(illiaDanchuk);
        driverService.create(nazarKovalenko);
        System.out.println(driverService.getAll());

        List<Driver> drivers = new ArrayList<>();
        drivers.add(petroVasylenko);
        drivers.add(illiaDanchuk);
        drivers.add(nazarKovalenko);

        CarService carService = (CarService) injector.getInstance(CarService.class);

        Car carMazda = new Car(mazda, "6", drivers);
        carMazda.setDrivers(List.of(nazarKovalenko));
        Car carDodge = new Car(dodge, "challenger", drivers);
        carDodge.setDrivers(drivers);
        Car carZaz = new Car(autoZaz, "forza", drivers);
        carService.create(carMazda);
        carService.create(carDodge);
        carService.create(carZaz);
        System.out.println(carService.getAll());
        carService.removeDriverFromCar(illiaDanchuk, carDodge);
        Driver nikitaMarchuk = new Driver("Nikita Marchuk", "DDD 44444");
        driverService.create(nikitaMarchuk);
        carService.addDriverToCar(nikitaMarchuk, carZaz);

        carZaz.setModel("Vida");
        System.out.println(carService.update(carZaz));

        System.out.println(carService.getAllByDriver(petroVasylenko.getId()));

        System.out.println(carService.delete(carMazda.getId()));

        System.out.println(carService.get(carDodge.getId()));
    }
}
