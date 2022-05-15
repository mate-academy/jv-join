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
        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);

        Driver driverOleh = new Driver("Oleh", "11111");
        driverOleh = driverService.create(driverOleh);

        Driver driverMaksym = new Driver("Maksym", "123422");
        driverMaksym = driverService.create(driverMaksym);

        Driver driverMariusz = new Driver("Mariusz", "222232");
        driverMariusz = driverService.create(driverMariusz);

        ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer audi = new Manufacturer("Audi", "Germany");
        audi = manufacturerService.create(audi);

        Manufacturer bmw = new Manufacturer("Bmw", "Germany");
        bmw = manufacturerService.create(bmw);

        Car carAudi = new Car("s7", audi);
        List<Driver> driversAudi = new ArrayList<>();
        driversAudi.add(driverOleh);
        carAudi.setDrivers(driversAudi);

        Car carBmw = new Car("m8", bmw);
        List<Driver> driversM8 = new ArrayList<>();
        driversM8.add(driverOleh);
        driversM8.add(driverMaksym);
        carBmw.setDrivers(driversM8);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        carAudi = carService.create(carAudi);
        System.out.println(carAudi);
        carAudi.setModel("s8");
        carAudi = carService.update(carAudi);
        carBmw = carService.create(carBmw);
        System.out.println(carAudi);

        boolean delete = carService.delete(carAudi.getId());
        System.out.println("Is deleted " + carAudi.getModel() + "? " + delete);
        System.out.println("Get all");
        carService.getAll().forEach(System.out::println);
        System.out.println("Get by Driver " + driverOleh);
        carService.getAllByDriver(driverOleh.getId()).forEach(System.out::println);
        carService.addDriverToCar(driverMariusz, carBmw);
        carService.removeDriverFromCar(driverMaksym, carBmw);
        carService.get(carBmw.getId());
    }
}
