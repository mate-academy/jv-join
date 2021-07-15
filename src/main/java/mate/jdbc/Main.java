package mate.jdbc;

import java.util.Arrays;
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
        final DriverService driverService = (DriverService) injector
                .getInstance(DriverService.class);
        final ManufacturerService manufacturerService = (ManufacturerService) injector
                .getInstance(ManufacturerService.class);
        final CarService carService = (CarService) injector.getInstance(CarService.class);

        Driver taras = driverService.create(new Driver("Taras", "EFG 72932"));
        Driver spiderMan = driverService.create(new Driver("Piter", "XNN 92761"));
        Driver mao = driverService.create(new Driver("Mao", "TYI 48752"));
        Driver sara = driverService.create(new Driver("Conor", "SGQ 10066"));

        Manufacturer pejo = manufacturerService.create(new Manufacturer("PEUGEOT", "France"));
        Manufacturer geely = manufacturerService.create(new Manufacturer("GEELY", "China"));

        Car carGeely = new Car("Emgrand 7", geely);
        carGeely.setDrivers(Arrays.asList(taras, mao, sara));
        carGeely = carService.create(carGeely);
        Car carPejo = new Car("3008", pejo);
        carPejo.setDrivers(Arrays.asList(spiderMan, mao, sara));
        carPejo = carService.create(carPejo);

        System.out.println("________________");
        carService.getAll().forEach(System.out::println);
        System.out.println("________________");

        carPejo.setModel("2008");
        carPejo.setDrivers(Arrays.asList(spiderMan, sara));
        carService.update(carPejo);
        carService.delete(carGeely.getId());

        carService.getAll().forEach(System.out::println);
        System.out.println("________________");
    }
}
