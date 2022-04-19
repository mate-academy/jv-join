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
        ManufacturerService manufacturerService
                = (ManufacturerService) injector.getInstance(ManufacturerService.class);

        Manufacturer manufacturerTesla = new Manufacturer("Tesla", "USA");
        Manufacturer manufacturerAudi = new Manufacturer("Audi", "Germany");
        manufacturerService.create(manufacturerTesla);
        manufacturerService.create(manufacturerAudi);

        Driver driverTanya = new Driver("Tanya", "123567");
        Driver driverSonya = new Driver("Sonya", "5654634");
        Driver driverKseniia = new Driver("Kseniia", "921333567");
        Driver driverBohdan = new Driver("Bohdan", "42356457");

        DriverService driverService
                = (DriverService) injector.getInstance(DriverService.class);
        driverService.create(driverTanya);
        driverService.create(driverSonya);
        driverService.create(driverKseniia);
        driverService.create(driverBohdan);

        List<Driver> driversTesla = new ArrayList<>();
        driversTesla.add(driverTanya);
        driversTesla.add(driverSonya);
        Car carTeslaS = new Car("Model S", manufacturerTesla, driversTesla);

        List<Driver> driversAudi = new ArrayList<>();
        driversTesla.add(driverKseniia);
        driversTesla.add(driverBohdan);
        Car carAudiA5 = new Car("Model S", manufacturerAudi, driversAudi);

        CarService carService
                = (CarService) injector.getInstance(CarService.class);
        carService.create(carTeslaS);
        carService.create(carAudiA5);

        System.out.println(carService.getAllByDriver(driverTanya.getId()));
        carService.addDriverToCar(driverTanya, carAudiA5);
        carService.getAll().forEach(System.out::println);
        carService.delete(driverSonya.getId());
        carService.removeDriverFromCar(driverSonya, carTeslaS);
        System.out.println(carService.getAllByDriver(driverSonya.getId()));
        carService.delete(carAudiA5.getId());
    }
}
