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
    private static final ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static final DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static final CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer zaz = manufacturerService.create(new Manufacturer("Zaz", "Ukraine"));
        Manufacturer dacia = manufacturerService.create(new Manufacturer("Dacia", "Romania"));
        Manufacturer seat = manufacturerService.create(new Manufacturer("Seat", "Spain"));

        final Driver joey = driverService.create(new Driver("Joey", "КАІ 12345"));
        final Driver ross = driverService.create(new Driver("Ross", "ГЕН 23456"));
        final Driver chandler = driverService.create(new Driver("Chandler", "ПАТ 34567"));
        final Driver monica = driverService.create(new Driver("Monica", "ТОВ 45678"));
        final Driver fibi = driverService.create(new Driver("Fibi", "ТОН 45678"));

        final Car zazSlavuta = carService.create(new Car("Slavuta", zaz,
                new ArrayList<>(List.of(joey, chandler))));
        final Car daciaLogan = carService.create(new Car("Logan", dacia,
                new ArrayList<>(List.of(ross))));
        final Car seatLeon = carService.create(new Car("Leon", seat,
                new ArrayList<>(List.of(monica))));

        zaz.setName("ZAZ");
        System.out.println(manufacturerService.update(zaz));
        System.out.println(manufacturerService.get(dacia.getId()));
        System.out.println(manufacturerService.delete(seat.getId()));
        System.out.println(manufacturerService.getAll());

        joey.setLicenseNumber("КАІ 54321");
        System.out.println(driverService.update(joey));
        System.out.println(driverService.get(ross.getId()));
        System.out.println(driverService.delete(chandler.getId()));
        System.out.println(driverService.getAll());

        zazSlavuta.setModel("Vida");
        System.out.println(carService.update(zazSlavuta));
        System.out.println(carService.get(zazSlavuta.getId()));
        System.out.println(carService.delete(seatLeon.getId()));
        System.out.println(carService.getAll());

        carService.addDriverToCar(fibi, zazSlavuta);
        carService.removeDriverFromCar(joey, zazSlavuta);

        carService.getAllByDriver(joey.getId()).forEach(System.out::println);
        carService.getAll().forEach(System.out::println);
    }
}
