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
    private static Injector injector = Injector.getInstance("mate.jdbc");
    private static ManufacturerService manufacturerService =
            (ManufacturerService) injector.getInstance(ManufacturerService.class);
    private static DriverService driverService =
            (DriverService) injector.getInstance(DriverService.class);
    private static CarService carService =
            (CarService) injector.getInstance(CarService.class);

    public static void main(String[] args) {
        Manufacturer audi = new Manufacturer("audi", "Germany");
        Manufacturer vw = new Manufacturer("vw", "Germany");
        Manufacturer lada = new Manufacturer("lada", "USSR");

        manufacturerService.create(audi);
        manufacturerService.create(vw);
        manufacturerService.create(lada);

        manufacturerService.getAll().forEach(System.out::println);

        Driver firstDriver = new Driver("Petrovich", "111111");
        Driver secondDriver = new Driver("Ochkovich", "22222");
        Driver thirdDriver = new Driver("Fuflovich", "33333");

        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);

        driverService.getAll().forEach(System.out::println);

        Car a5 = new Car("a5", manufacturerService.get(5L));
        Car a4 = new Car("a4", manufacturerService.get(5L));
        Car a3 = new Car("a3", manufacturerService.get(5L));

        a5.setDrivers(List.of(driverService.get(7L), driverService.get(8L)));
        a4.setDrivers(List.of(driverService.get(8L), driverService.get(9L)));
        a3.setDrivers(List.of(driverService.get(9L), driverService.get(7L)));

        carService.create(a5);
        carService.create(a4);
        carService.create(a3);

        carService.getAll().forEach(System.out::println);

        a3 = carService.get(12L);
        a3.setManufacturer(manufacturerService.get(5L));
        a3.setDrivers(List.of(driverService.get(8L)));
        System.out.println(carService.update(a3));

        carService.setDriverToCar(driverService.get(9L), a4);
        carService.removeDriver(driverService.get(9L), a4);

        a4.getDrivers().forEach(System.out::println);

        carService.getAllByDriver(8L).forEach(System.out::println);
    }
}
