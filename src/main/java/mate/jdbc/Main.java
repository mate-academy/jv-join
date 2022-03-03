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
    public static void main(String[] args) {
        final Injector injector = Injector.getInstance("mate.jdbc");
        final CarService carService = (CarService) injector.getInstance(CarService.class);
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        final DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver vadym = driverService.create(
                new Driver("Vadym", "CC 9876 DD"));
        List<Driver> drivers1 = new ArrayList<>();
        drivers1.add(driverService.create(new Driver("Bob", "AA 1234 BB")));
        drivers1.add(driverService.create(new Driver("Alice", "BB 4567 CC")));
        drivers1.add(vadym);

        Driver kostya = driverService.create(
                new Driver("Kostya", "KH 1234 DD"));
        List<Driver> drivers2 = new ArrayList<>();
        drivers2.add(driverService.create(new Driver("Bohdan", "AA 6345 AA")));
        drivers2.add(kostya);

        Manufacturer tesla = manufacturerService.create(
                new Manufacturer("Tesla", "USA"));
        Car car1 = new Car("model S", tesla, drivers1);
        Manufacturer mercedes = manufacturerService.create(
                new Manufacturer("Mercedes Benz", "Germany"));
        Car car2 = new Car("GLA", mercedes, drivers2);

        carService.create(car1);
        carService.create(car2);
        System.out.println(carService.get(2L));
        System.out.println();
        car2.setModel("other model");
        carService.update(car2);
        carService.delete(1L);

        carService.getAll().forEach(System.out::println);
        System.out.println();

        carService.addDriverToCar(vadym, car2);
        carService.removeDriverFromCar(kostya, car2);
        carService.get(2L).getDrivers().forEach(System.out::println);
    }
}
