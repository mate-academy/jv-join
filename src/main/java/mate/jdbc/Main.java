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
        final ManufacturerService manufacturerService =
                (ManufacturerService) injector.getInstance(ManufacturerService.class);
        Manufacturer manufacturerNissan = new Manufacturer("nissan", "Japan");
        Manufacturer manufacturerAudi = new Manufacturer("audi", "Germany");
        manufacturerService.create(manufacturerNissan);
        manufacturerService.create(manufacturerAudi);

        Driver firstDriver = new Driver("Michael", "12345");
        Driver secondDriver = new Driver("Antonio", "123");
        Driver thirdDriver = new Driver("Robert", "1234");

        DriverService driverService =
                (DriverService) injector.getInstance(DriverService.class);
        driverService.create(firstDriver);
        driverService.create(secondDriver);
        driverService.create(thirdDriver);

        List<Driver> driversNissan = new ArrayList<>();
        List<Driver> driversAudi = new ArrayList<>();

        driversNissan.add(firstDriver);
        driversNissan.add(secondDriver);
        driversAudi.add(thirdDriver);
        driversAudi.add(firstDriver);

        CarService carService =
                (CarService) injector.getInstance(CarService.class);
        Car nissan = new Car("Juke", manufacturerNissan, driversNissan);
        Car bmw = new Car("M5", manufacturerAudi, driversAudi);
        carService.create(nissan);
        carService.create(bmw);

        carService.getAllByDriver(nissan.getId());
        nissan.setManufacturer(manufacturerService.get(manufacturerNissan.getId()));
        carService.update(nissan);
        carService.getAll().forEach(System.out::println);
        carService.removeDriverFromCar(secondDriver, nissan);
        carService.getAllByDriver(firstDriver.getId()).forEach(System.out::println);

        carService.delete(bmw.getId());
    }
}
