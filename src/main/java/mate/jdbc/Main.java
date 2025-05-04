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
    private static final Injector injector = Injector.getInstance(Main.class.getPackageName());

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        Manufacturer bmw = new Manufacturer("BMW", "Germany");
        Manufacturer kia = new Manufacturer("KIA", "South Korea");
        Manufacturer seat = new Manufacturer("SEAT", "Spain");
        final List<Driver> drivers = new ArrayList<>();
        final Driver misha = new Driver("Misha", "11111111111");
        final Driver olya = new Driver("Olya", "22222222222");
        final Driver oleg = new Driver("Oleg", "33333333333");
        final Driver sasha = new Driver("Sasha", "444444444444");
        final Driver katya = new Driver("Katya", "555555555555");
        final Car xFive = new Car("x5", bmw);
        final Car evSix = new Car("ev6", kia);
        final Car ateca = new Car("ateca", seat);
        manufacturerService.create(bmw);
        manufacturerService.create(kia);
        manufacturerService.create(seat);
        System.out.println("Manufacturers successfully added to DB!");
        driverService.create(misha);
        driverService.create(olya);
        driverService.create(oleg);
        driverService.create(sasha);
        driverService.create(katya);
        System.out.println("Drivers successfully added to DB!!");
        drivers.add(misha);
        drivers.add(oleg);
        xFive.setDrivers(drivers);
        carService.create(xFive);

        drivers.clear();
        drivers.add(olya);
        drivers.add(katya);
        evSix.setDrivers(drivers);
        carService.create(evSix);

        drivers.clear();
        drivers.add(sasha);
        ateca.setDrivers(drivers);
        carService.create(ateca);
        System.out.println("Cars successfully added to DB!!\n");
        System.out.println("########################### ALL CARS ###########################");
        System.out.println(carService.getAll());
        System.out.println("\n########################## UPDATE CAR ########################");
        drivers.add(katya);
        System.out.println(carService.update(ateca));
        System.out.println("AFTER: ");
        System.out.println(carService.getAll());
        System.out.println("\n########################### GET CAR ##########################");
        System.out.println(kia);
        System.out.println(carService.get(kia.getId()));
        System.out.println("\n####################### GET CARS BY DRIVER ###################");
        System.out.println(katya);
        System.out.println(carService.getAllByDriver(katya.getId()));
        System.out.println("\n########################## DELETE CAR ########################");
        System.out.println(bmw);
        System.out.println(carService.delete(bmw.getId()));
        System.out.println("AFTER:");
        System.out.println(carService.getAll());
        System.out.println("\n################################################################");
        System.out.println("########################## TESTS SUCCESS ########################");
        System.out.println("################################################################");
    }
}
