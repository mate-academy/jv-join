package mate.jdbc;

import mate.jdbc.lib.Injector;
import mate.jdbc.model.Car;
import mate.jdbc.model.Driver;
import mate.jdbc.model.Manufacturer;
import mate.jdbc.service.CarService;
import mate.jdbc.service.DriverService;
import mate.jdbc.service.ManufacturerService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Injector injector = Injector.getInstance("mate.jdbc");

    public static void main(String[] args) {
        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer kia = new Manufacturer("Kia", "Korea");
        Manufacturer deo = new Manufacturer("Daewoo", "Ukraine");
        manufacturerService.create(kia);
        manufacturerService.create(deo);

        System.out.println(kia);
        System.out.println(deo);
        System.out.println("\n Created 3 manufacturers \n");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        List<Driver> kiaDrivers = new ArrayList<>();
        List<Driver> deoDrivers = new ArrayList<>();
        Driver bohdan = new Driver("Bohdan", "UA113300");
        Driver eugen = new Driver("Eugen", "UA004432");
        Driver mahmud = new Driver("Mahmud", "UA4553234");
        driverService.create(bohdan);
        driverService.create(eugen);
        driverService.create(mahmud);
        kiaDrivers.add(bohdan);
        kiaDrivers.add(eugen);
        deoDrivers.add(mahmud);

        System.out.println("\n Created 4 drivers \n");

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car kiaCar = new Car("Sportage", kia, kiaDrivers);
        Car deoCar = new Car("Lanos", deo, deoDrivers);
        carService.create(kiaCar);
        carService.create(deoCar);

        System.out.println("\n Created 3 cars \n");

        System.out.println(carService.getAll());

        carService.addDriverToCar(eugen, deoCar);
        System.out.println(carService.get(deoCar.getId()));
    }
}
