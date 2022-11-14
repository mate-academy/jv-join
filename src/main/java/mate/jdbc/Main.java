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
        Manufacturer manufacturerBmv = new Manufacturer("Bmv", "Germany");
        Manufacturer manufacturerToyota = new Manufacturer("Toyota", "Japan");
        manufacturerService.create(manufacturerBmv);
        manufacturerService.create(manufacturerToyota);

        DriverService driverService = (DriverService) injector.getInstance(DriverService.class);
        Driver driverBrian = new Driver("Brian", "111111");
        Driver driverDominic = new Driver("Dominic", "222222");
        Driver driverHan = new Driver("Han", "333333");
        Driver driverRoman = new Driver("Roman", "444444");
        driverService.create(driverBrian);
        driverService.create(driverDominic);
        driverService.create(driverHan);
        driverService.create(driverRoman);
        List<Driver> driversX6 = new ArrayList<>();
        driversX6.add(driverHan);
        driversX6.add(driverRoman);
        List<Driver> driversCamry = new ArrayList<>();
        driversCamry.add(driverBrian);

        CarService carService = (CarService) injector.getInstance(CarService.class);
        Car carX6 = new Car("X6", manufacturerBmv, driversX6);
        Car carCamry = new Car("Camry", manufacturerToyota, driversCamry);
        carService.create(carX6);
        carService.create(carCamry);
        carService.getAll().forEach(System.out::println);
        carX6.setModel("X7");
        carService.update(carX6);
        driversCamry.add(driverDominic);
        carCamry.setDrivers(driversCamry);
        carService.update(carCamry);
        System.out.println(carService.get(carX6.getId()));
        System.out.println(carService.get(carCamry.getId()));
        carService.delete(carX6.getId());
        carService.removeDriverFromCar(driverBrian, carCamry);
        carService.addDriverToCar(driverHan, carCamry);
        carService.getAllByDriver(driverHan.getId()).forEach(System.out::println);
    }
}
